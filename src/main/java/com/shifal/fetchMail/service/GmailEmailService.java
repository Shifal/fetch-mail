package com.shifal.fetchMail.service;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.*;
import com.shifal.fetchMail.config.GmailClientProvider;
import com.shifal.fetchMail.model.EmailMessage;
import com.shifal.fetchMail.model.GmailQueryCriteria;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.shifal.fetchMail.model.GmailFetchRequest;
import com.shifal.fetchMail.model.EmailFullWithAttachments;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartBody;

import java.time.Instant;
import java.util.*;

@Service
public class GmailEmailService implements EmailService {

    private final GmailClientProvider clientProvider;

    @Value("${app.gmail.user-id:me}")
    private String userId;

    public GmailEmailService(GmailClientProvider clientProvider) {
        this.clientProvider = clientProvider;
    }

    @Override
    public List<EmailMessage> search(GmailQueryCriteria criteria) throws Exception {
        Gmail gmail = clientProvider.getClient();
        String query = buildGmailQuery(criteria);

        Gmail.Users.Messages.List listCall = gmail.users()
                .messages()
                .list(userId)
                .setQ(query)
                .setMaxResults(Optional.ofNullable(criteria.getMaxResults()).map(Long::valueOf).orElse(20L));

        if (criteria.getPageToken() != null && !criteria.getPageToken().isBlank()) {
            listCall.setPageToken(criteria.getPageToken());
        }

        ListMessagesResponse resp = listCall.execute();
        if (resp.getMessages() == null || resp.getMessages().isEmpty()) return Collections.emptyList();

        List<EmailMessage> result = new ArrayList<>();
        for (Message m : resp.getMessages()) {
            EmailMessage dto = fetchAndMap(gmail, m.getId());

            // Gmail query can't fully filter "To", so post-filter
            if (criteria.getTo() == null || dto.getTo().contains(criteria.getTo())) {
                result.add(dto);
            }
        }
        return result;
    }

    @Override
    public EmailMessage getById(String id) throws Exception {
        return fetchAndMap(clientProvider.getClient(), id);
    }

    private String buildGmailQuery(GmailQueryCriteria c) {
        List<String> parts = new ArrayList<>();
        if (notBlank(c.getSubject())) parts.add("subject:\"" + escape(c.getSubject()) + "\"");
        if (notBlank(c.getFrom())) parts.add("from:\"" + escape(c.getFrom()) + "\"");
        if (notBlank(c.getTo())) parts.add("to:\"" + escape(c.getTo()) + "\"");
        return parts.isEmpty() ? "newer_than:1d" : String.join(" ", parts);
    }

    private boolean notBlank(String s) {
        return s != null && !s.isBlank();
    }

    private String escape(String s) {
        return s.replace("\"", "\\\"");
    }

    private EmailMessage fetchAndMap(Gmail gmail, String messageId) throws Exception {
        Message msg = gmail.users()
                .messages()
                .get(userId, messageId)
                .setFormat("metadata")
                .setMetadataHeaders(List.of(
                        "Subject", "From", "To", "Date", "Reply-To", "Return-Path", "Authentication-Results"
                ))
                .execute();

        Map<String, String> headers = headersToMap(msg.getPayload().getHeaders());

        EmailMessage dto = new EmailMessage();
        dto.setId(msg.getId());
        dto.setThreadId(msg.getThreadId());
        dto.setSnippet(Optional.ofNullable(msg.getSnippet()).orElse(""));
        dto.setSubject(headers.getOrDefault("Subject", ""));
        dto.setFrom(headers.getOrDefault("From", ""));
        dto.setTo(headers.getOrDefault("To", ""));
        dto.setReplyTo(headers.getOrDefault("Reply-To", ""));
        dto.setDate(Instant.ofEpochMilli(Optional.ofNullable(msg.getInternalDate()).orElse(0L)));
        dto.setMailedBy(getMailedBy(dto.getFrom(), headers));
        dto.setSignedBy(getSignedBy(headers));
        dto.setLabels(msg.getLabelIds());

        return dto;
    }

    private Map<String, String> headersToMap(List<MessagePartHeader> headers) {
        if (headers == null) return Collections.emptyMap();
        Map<String, String> map = new LinkedHashMap<>();
        for (MessagePartHeader h : headers) map.put(h.getName(), h.getValue());
        return map;
    }

    private String getMailedBy(String from, Map<String, String> headers) {
        String domain = extractDomain(from);
        if (domain != null) return domain;
        return Optional.ofNullable(extractDomain(headers.get("Return-Path"))).orElse("");
    }

    private String extractDomain(String email) {
        if (email == null || email.isBlank()) return null;
        String v = email.trim();
        int lt = v.indexOf('<'), gt = v.indexOf('>');
        if (lt >= 0 && gt > lt) v = v.substring(lt + 1, gt);
        int at = v.lastIndexOf('@');
        return (at > 0 && at < v.length() - 1) ? v.substring(at + 1).replace(">", "").replace("\"", "").trim() : null;
    }

    private String getSignedBy(Map<String, String> headers) {
        String auth = headers.getOrDefault("Authentication-Results", "");
        if (auth.contains("dkim=pass") && auth.contains("header.d=")) {
            int idx = auth.indexOf("header.d=");
            String sub = auth.substring(idx + 9);
            int end = sub.indexOf(';');
            return (end > 0 ? sub.substring(0, end) : sub).trim();
        }
        return "";
    }

    public List<EmailFullWithAttachments> fetchEmailsWithAttachments(GmailFetchRequest request) throws Exception {
        Gmail gmail = clientProvider.getClient();
        List<Message> messages = new ArrayList<>();

        if (request.getTo() != null && !request.getTo().isBlank()) {
            messages.add(gmail.users().messages().get(userId, request.getTo()).setFormat("full").execute());
        } else {
            StringBuilder query = new StringBuilder();
            if (request.getFrom() != null && !request.getFrom().isBlank()) query.append("from:").append(request.getFrom()).append(" ");
            if (request.getSubject() != null && !request.getSubject().isBlank()) query.append("subject:").append(request.getSubject()).append(" ");
            if (request.getTo() != null && !request.getTo().isBlank()) query.append("to:").append(request.getTo()).append(" ");
            if (query.isEmpty()) query.append("newer_than:1d");

            ListMessagesResponse resp = gmail.users().messages().list(userId)
                    .setQ(query.toString().trim())
                    .setMaxResults(Optional.ofNullable(request.getMaxResults()).map(Long::valueOf).orElse(20L))
                    .execute();

            if (resp.getMessages() != null) {
                for (Message m : resp.getMessages()) {
                    messages.add(gmail.users().messages().get(userId, m.getId()).setFormat("full").execute());
                }
            }
        }

        List<EmailFullWithAttachments> result = new ArrayList<>();
        for (Message msg : messages) {
            EmailFullWithAttachments dto = new EmailFullWithAttachments();

            Map<String, String> headers = new LinkedHashMap<>();
            for (MessagePartHeader h : msg.getPayload().getHeaders()) headers.put(h.getName(), h.getValue());

            dto.setId(msg.getId());
            dto.setThreadId(msg.getThreadId());
            dto.setSnippet(Optional.ofNullable(msg.getSnippet()).orElse(""));
            dto.setSubject(headers.getOrDefault("Subject", ""));
            dto.setFrom(headers.getOrDefault("From", ""));
            dto.setTo(headers.getOrDefault("To", ""));
            dto.setReplyTo(headers.getOrDefault("Reply-To", ""));
            dto.setDate(Instant.ofEpochMilli(Optional.ofNullable(msg.getInternalDate()).orElse(0L)));
            dto.setMailedBy(getMailedBy(dto.getFrom(), headers));
            dto.setSignedBy(getSignedBy(headers));
            dto.setLabels(msg.getLabelIds());

            Map<String, String> attachments = new LinkedHashMap<>();
            extractAttachmentsFull(msg.getPayload(), attachments, gmail, msg.getId());
            dto.setAttachmentFiles(attachments);

            result.add(dto);
        }

        return result;
    }



    // Recursive method to extract attachments
    private void extractAttachmentsFull(MessagePart part, Map<String, String> attachments, Gmail gmail, String messageId) throws Exception {
        if (part.getParts() != null && !part.getParts().isEmpty()) {
            for (MessagePart p : part.getParts()) {
                extractAttachmentsFull(p, attachments, gmail, messageId);
            }
        } else {
            String filename = part.getFilename();
            String mimeType = part.getMimeType();

            if (filename != null && !filename.isBlank() &&
                    (mimeType.contains("application/pdf") ||
                            mimeType.contains("application/msword") ||
                            mimeType.contains("application/vnd.openxmlformats-officedocument") ||
                            mimeType.startsWith("image/"))) {

                if (part.getBody() != null && part.getBody().getAttachmentId() != null) {
                    String attachId = part.getBody().getAttachmentId();
                    MessagePartBody attachPart = gmail.users().messages()
                            .attachments()
                            .get(userId, messageId, attachId)
                            .execute();

                    // Decode Gmail URL-safe Base64 and encode normal Base64
                    byte[] fileBytes = Base64.getUrlDecoder().decode(attachPart.getData());
                    String base64Content = Base64.getEncoder().encodeToString(fileBytes);

                    attachments.put(filename, base64Content);
                }
            }
        }
    }

}
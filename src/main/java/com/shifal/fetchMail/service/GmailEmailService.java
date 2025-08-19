package com.shifal.fetchMail.service;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.*;
import com.shifal.fetchMail.config.GmailClientProvider;
import com.shifal.fetchMail.model.EmailMessage;
import com.shifal.fetchMail.model.GmailQueryCriteria;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

        Gmail.Users.Messages.List listCall = gmail.users().messages()
                .list(userId).setQ(query)
                .setMaxResults(Long.valueOf(
                        Optional.ofNullable(criteria.getMaxResults()).orElse(20)));

        if (criteria.getPageToken() != null && !criteria.getPageToken().isBlank()) {
            listCall.setPageToken(criteria.getPageToken());
        }

        ListMessagesResponse resp = listCall.execute();
        if (resp.getMessages() == null || resp.getMessages().isEmpty()) {
            return Collections.emptyList();
        }

        List<EmailMessage> result = new ArrayList<>();
        for (Message m : resp.getMessages()) {
            EmailMessage dto = fetchAndMap(gmail, m.getId());

            // Post-filters for headers Gmail query can’t handle
            if (criteria.getTo() != null && !dto.getTo().contains(criteria.getTo())) continue;

            result.add(dto);
        }
        return result;
    }

    @Override
    public EmailMessage getById(String id) throws Exception {
        Gmail gmail = clientProvider.getClient();
        return fetchAndMap(gmail, id);
    }

    private String buildGmailQuery(GmailQueryCriteria c) {
        List<String> parts = new ArrayList<>();
        if (c.getSubject() != null && !c.getSubject().isBlank()) {
            parts.add("subject:\"" + escape(c.getSubject()) + "\"");
        }
        if (c.getFrom() != null && !c.getFrom().isBlank()) {
            parts.add("from:\"" + escape(c.getFrom()) + "\"");
        }
        if (c.getTo() != null && !c.getTo().isBlank()) {
            parts.add("to:\"" + escape(c.getTo()) + "\"");
        }
        if (parts.isEmpty()) {
            parts.add("newer_than:1d"); // safety default
        }
        return String.join(" ", parts);
    }

    private String escape(String s) {
        return s.replace("\"", "\\\"");
    }

    private EmailMessage fetchAndMap(Gmail gmail, String messageId) throws Exception {
        Message msg = gmail.users().messages().get(userId, messageId)
                .setFormat("metadata")
                .setMetadataHeaders(List.of(
                        "Subject","From","To","Date","Reply-To","Return-Path","Authentication-Results"
                ))
                .execute();

        Map<String, String> headers = headersToMap(msg.getPayload().getHeaders());

        EmailMessage dto = new EmailMessage();
        dto.setId(msg.getId());
        dto.setThreadId(msg.getThreadId());
        dto.setSnippet(msg.getSnippet() != null ? msg.getSnippet() : "");
        dto.setSubject(headers.getOrDefault("Subject", ""));
        dto.setFrom(headers.getOrDefault("From", ""));
        dto.setTo(headers.getOrDefault("To", ""));
        dto.setReplyTo(headers.getOrDefault("Reply-To", ""));
        dto.setDate(Instant.ofEpochMilli(Optional.ofNullable(msg.getInternalDate()).orElse(0L)));
        dto.setMailedBy(deriveMailedBy(dto.getFrom(), headers));
        dto.setSignedBy(extractSignedBy(headers));
        dto.setLabels(msg.getLabelIds());
        return dto;
    }

    private Map<String,String> headersToMap(List<MessagePartHeader> headers) {
        Map<String,String> map = new LinkedHashMap<>();
        if (headers == null) return map;
        for (MessagePartHeader h : headers) {
            map.put(h.getName(), h.getValue());
        }
        return map;
    }

    private String deriveMailedBy(String from, Map<String,String> headers) {
        String domain = extractDomainFromEmail(from);
        if (domain != null) return domain;
        domain = extractDomainFromEmail(headers.getOrDefault("Return-Path", ""));
        return domain != null ? domain : "";
    }

    private String extractDomainFromEmail(String headerVal) {
        if (headerVal == null) return null;
        String v = headerVal.trim();
        int lt = v.indexOf('<');
        int gt = v.indexOf('>');
        if (lt >= 0 && gt > lt) v = v.substring(lt+1, gt);
        int at = v.lastIndexOf('@');
        if (at > 0 && at < v.length() - 1) {
            return v.substring(at + 1).replace(">", "").replace("\"", "").trim();
        }
        return null;
    }

    private String extractSignedBy(Map<String,String> headers) {
        String auth = headers.getOrDefault("Authentication-Results", "");
        if (auth.contains("dkim=pass") && auth.contains("header.d=")) {
            int idx = auth.indexOf("header.d=");
            if (idx >= 0) {
                String sub = auth.substring(idx + 9);
                int end = sub.indexOf(';');
                return (end > 0 ? sub.substring(0, end) : sub).trim();
            }
        }
        return "";
    }
}

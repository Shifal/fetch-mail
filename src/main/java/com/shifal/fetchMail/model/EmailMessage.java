package com.shifal.fetchMail.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

/**
 * DTO returned by REST API. Keeps Gmail internals out of the public model.
 */
public class EmailMessage {
    private String id;
    private String threadId;
    private String snippet;
    private String subject;
    private String from;
    private String to;
    private String replyTo;
    private Instant date;
    @JsonProperty("mailed-by")
    private String mailedBy;
    private String signedBy;
    private List<String> labels;

    public EmailMessage() {}

    // Getters / setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getThreadId() { return threadId; }
    public void setThreadId(String threadId) { this.threadId = threadId; }

    public String getSnippet() { return snippet; }
    public void setSnippet(String snippet) { this.snippet = snippet; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }

    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }

    public String getReplyTo() { return replyTo; }
    public void setReplyTo(String replyTo) { this.replyTo = replyTo; }

    public Instant getDate() { return date; }
    public void setDate(Instant date) { this.date = date; }

    public String getMailedBy() { return mailedBy; }
    public void setMailedBy(String mailedBy) { this.mailedBy = mailedBy; }

    public String getSignedBy() { return signedBy; }
    public void setSignedBy(String signedBy) { this.signedBy = signedBy; }

    public List<String> getLabels() { return labels; }
    public void setLabels(List<String> labels) { this.labels = labels; }
}

package com.shifal.fetchMail.model;

public class GmailFetchRequest {
    private String id;
    private String from;
    private String to;
    private String subject;

    // Optional: how many emails to fetch if no ID provided
    private Integer maxResults = 10;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }

    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public Integer getMaxResults() { return maxResults; }
    public void setMaxResults(Integer maxResults) { this.maxResults = maxResults; }
}

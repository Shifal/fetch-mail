package com.shifal.model;

public class Email {
    private String subject;
    private String from;
    private String mailedBy;
    private String body;

    public Email(String subject, String from, String mailedBy, String body) {
        this.subject = subject;
        this.from = from;
        this.mailedBy = mailedBy;
        this.body = body;
    }

    // Getters
    public String getSubject() { return subject; }
    public String getFrom() { return from; }
    public String getMailedBy() { return mailedBy; }
    public String getBody() { return body; }
}

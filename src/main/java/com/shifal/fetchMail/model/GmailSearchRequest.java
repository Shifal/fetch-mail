package com.shifal.fetchMail.model;

/**
 * Represents a request body for searching emails.
 * Supports filtering by 'from', 'to', and 'subject' fields.
 */
public class GmailSearchRequest {

    private String to;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

}

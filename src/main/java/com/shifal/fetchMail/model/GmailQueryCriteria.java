package com.shifal.fetchMail.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * Simple DTO capturing search filters.
 */
public class GmailQueryCriteria {
    private String subject;
    private String from;
    private String to;

    @Min(1)
    @Max(100)
    private Integer maxResults = 20;

    private String pageToken;

    public GmailQueryCriteria() {}

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }

    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }

    public Integer getMaxResults() { return maxResults; }
    public void setMaxResults(Integer maxResults) { this.maxResults = maxResults; }

    public String getPageToken() { return pageToken; }
    public void setPageToken(String pageToken) { this.pageToken = pageToken; }
}

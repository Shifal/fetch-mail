package com.shifal.fetchMail.model;

import java.util.List;

public class EmailWithAttachments {
    private String to;
    private String from;
    private String subject;
    private List<String> attachmentFiles; // Just filenames + base64 placeholder

    public String getId() { return to; }
    public void setId(String to) { this.to = to; }

    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public List<String> getAttachmentFiles() { return attachmentFiles; }
    public void setAttachmentFiles(List<String> attachmentFiles) { this.attachmentFiles = attachmentFiles; }
}

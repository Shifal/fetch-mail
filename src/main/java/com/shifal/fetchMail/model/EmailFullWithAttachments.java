package com.shifal.fetchMail.model;

import java.util.Map;

public class EmailFullWithAttachments extends EmailMessage {
    // filename -> base64 content
    private Map<String, String> attachmentFiles;

    public Map<String, String> getAttachmentFiles() { return attachmentFiles; }
    public void setAttachmentFiles(Map<String, String> attachmentFiles) { this.attachmentFiles = attachmentFiles; }
}

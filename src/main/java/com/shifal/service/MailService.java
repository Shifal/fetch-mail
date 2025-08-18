package com.shifal.service;

import com.shifal.model.Email;
import com.shifal.resource.GmailClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MailService {

    private final GmailClient gmailClient;

    public MailService(GmailClient gmailClient) {
        this.gmailClient = gmailClient;
    }

    public List<Email> getBySubject(String subject) {
        return gmailClient.fetchBySubject(subject);
    }

    public List<Email> getByFrom(String from) {
        return gmailClient.fetchByFrom(from);
    }

    public List<Email> getByMailedBy(String mailedBy) {
        return gmailClient.fetchByMailedBy(mailedBy);
    }
}

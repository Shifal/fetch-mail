package com.shifal.fetchMail.service;

import com.shifal.fetchMail.model.EmailMessage;
import com.shifal.fetchMail.model.GmailQueryCriteria;

import java.util.List;

/**
 * Service abstraction (DIP).
 */
public interface EmailService {
    List<EmailMessage> search(GmailQueryCriteria criteria) throws Exception;
    EmailMessage getById(String id) throws Exception;
}

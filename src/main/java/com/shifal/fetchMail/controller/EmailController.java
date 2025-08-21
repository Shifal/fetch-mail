package com.shifal.fetchMail.controller;

import com.shifal.fetchMail.model.EmailMessage;
import com.shifal.fetchMail.model.GmailQueryCriteria;
import com.shifal.fetchMail.service.EmailService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.shifal.fetchMail.model.GmailFetchRequest;
import com.shifal.fetchMail.model.EmailFullWithAttachments;
import com.shifal.fetchMail.service.GmailEmailService;

import java.util.List;

@RestController
@RequestMapping("/api/emails")
@Validated
public class EmailController {

    private final EmailService service;

    public EmailController(EmailService service) {
        this.service = service;
    }

    /**
     * Search emails with optional criteria.
     * Automatically maps query parameters to GmailQueryCriteria.
     */
    @GetMapping
    public List<EmailMessage> search(@ModelAttribute @Validated GmailQueryCriteria criteria) throws Exception {
        return service.search(criteria);
    }

    /**
     * Get a single email by ID.
     */
    @GetMapping("/{id}")
    public EmailMessage getById(@PathVariable String id) throws Exception {
        return service.getById(id);
    }

    @PostMapping("/fetch")
    public List<EmailFullWithAttachments> fetchEmailsWithAttachments(@RequestBody GmailFetchRequest request) throws Exception {
        return ((GmailEmailService) service).fetchEmailsWithAttachments(request);
    }
}

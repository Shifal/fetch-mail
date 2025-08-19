package com.shifal.fetchMail.controller;

import com.shifal.fetchMail.model.EmailMessage;
import com.shifal.fetchMail.model.GmailQueryCriteria;
import com.shifal.fetchMail.service.EmailService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emails")
@Validated
public class EmailController {

    private final EmailService service;

    public EmailController(EmailService service) {
        this.service = service;
    }

    @GetMapping
    public List<EmailMessage> search(
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) String mailedBy,
            @RequestParam(required = false) String signedBy,
            @RequestParam(required = false) String replyTo,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) Integer maxResults,
            @RequestParam(required = false) String pageToken
    ) throws Exception {

        GmailQueryCriteria criteria = new GmailQueryCriteria();
        criteria.setSubject(subject);
        criteria.setFrom(from);
        criteria.setTo(to);
        criteria.setMailedBy(mailedBy);
        criteria.setSignedBy(signedBy);
        criteria.setReplyTo(replyTo);
        criteria.setMaxResults(maxResults);
        criteria.setPageToken(pageToken);

        return service.search(criteria);
    }

    @GetMapping("/{id}")
    public EmailMessage getById(@PathVariable String id) throws Exception {
        return service.getById(id);
    }
}

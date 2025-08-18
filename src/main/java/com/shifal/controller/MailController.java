package com.shifal.controller;

import com.shifal.model.Email;
import com.shifal.service.MailService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mail")
public class MailController {

    private final MailService mailService;

    public MailController(MailService mailService) {
        this.mailService = mailService;
    }

    @GetMapping("/subject/{keyword}")
    public List<Email> getBySubject(@PathVariable String keyword) {
        return mailService.getBySubject(keyword);
    }

    @GetMapping("/from/{from}")
    public List<Email> getByFrom(@PathVariable String from) {
        return mailService.getByFrom(from);
    }

    @GetMapping("/mailedby/{mailedBy}")
    public List<Email> getByMailedBy(@PathVariable String mailedBy) {
        return mailService.getByMailedBy(mailedBy);
    }
}

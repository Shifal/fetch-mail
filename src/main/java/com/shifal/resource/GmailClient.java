package com.shifal.resource;

import com.shifal.model.Email;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GmailClient {

    private final List<Email> mockEmails = List.of(
        new Email("Spring Boot Intro", "alice@example.com", "example.com", "Learn Spring Boot basics."),
        new Email("Meeting Reminder", "bob@work.com", "work.com", "Don’t forget our meeting at 5 PM."),
        new Email("Invoice #1234", "billing@shop.com", "shop.com", "Your invoice is attached."),
        new Email("Welcome!", "noreply@service.com", "service.com", "Thanks for signing up!")
    );

    public List<Email> fetchBySubject(String keyword) {
        return mockEmails.stream()
                .filter(email -> email.getSubject().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Email> fetchByFrom(String from) {
        return mockEmails.stream()
                .filter(email -> email.getFrom().equalsIgnoreCase(from))
                .collect(Collectors.toList());
    }

    public List<Email> fetchByMailedBy(String mailedBy) {
        return mockEmails.stream()
                .filter(email -> email.getMailedBy().equalsIgnoreCase(mailedBy))
                .collect(Collectors.toList());
    }
}

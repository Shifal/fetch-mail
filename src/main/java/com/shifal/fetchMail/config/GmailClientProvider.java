package com.shifal.fetchMail.config;

import com.google.api.services.gmail.Gmail;

/**
 * Provider interface for obtaining an authorized Gmail client.
 */
public interface GmailClientProvider {
    Gmail getClient() throws Exception;
}

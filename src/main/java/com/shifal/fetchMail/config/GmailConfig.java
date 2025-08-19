package com.shifal.fetchMail.config;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.List;

@Configuration
public class GmailConfig implements GmailClientProvider {

    private static final String APPLICATION_NAME = "FetchMailApp";
    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    @Value("${app.gmail.tokens-directory}")
    private String tokensDirectory;

    @Override
    public Gmail getClient() throws Exception {
        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        InputStream in = getClass().getClassLoader().getResourceAsStream("credentials.json");
        if (in == null) {
            throw new IllegalStateException("credentials.json not found in src/main/resources.");
        }

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, List.of(GmailScopes.GMAIL_READONLY))
                .setDataStoreFactory(new FileDataStoreFactory(Path.of(tokensDirectory).toFile()))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();

        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

        return new Gmail.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}

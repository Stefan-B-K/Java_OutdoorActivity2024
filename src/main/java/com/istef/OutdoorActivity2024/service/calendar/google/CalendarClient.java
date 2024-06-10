package com.istef.OutdoorActivity2024.service.calendar.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.istef.OutdoorActivity2024.OutdoorActivityApp;
import com.istef.OutdoorActivity2024.exceptios.CalendarException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;


class CalendarClient {

    private static Calendar _client;

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    // If modifying these scopes, delete your previously saved tokens/ folder.
    private static final List<String> SCOPES =
            Collections.singletonList(CalendarScopes.CALENDAR);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";


    private CalendarClient() {
    }

    static Calendar getClient() throws CalendarException {
        if (_client == null) {
            try {
                NetHttpTransport HTTP_TRANSPORT =
                        GoogleNetHttpTransport.newTrustedTransport();
                _client = new Calendar
                        .Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                        .setApplicationName(OutdoorActivityApp.APP_NAME)
                        .build();
            } catch (GeneralSecurityException | IOException e) {
                throw new CalendarException(e.getMessage());
            }
        }
        return _client;
    }

    static void initialUserAuth() throws CalendarException {
        try {
            NetHttpTransport HTTP_TRANSPORT =
                    GoogleNetHttpTransport.newTrustedTransport();
            getCredentials(HTTP_TRANSPORT);
        } catch (GeneralSecurityException | IOException | CalendarException e) {
            throw new CalendarException(e.getMessage());
        }
    }

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
            throws CalendarException {

        InputStream in = CalendarClient.class
                .getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new CalendarException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }

        try {
            GoogleClientSecrets clientSecrets =
                    GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow
                    .Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                    .setDataStoreFactory(
                            new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH)))
                    .setAccessType("offline")
                    .build();

            LocalServerReceiver receiver = new LocalServerReceiver.Builder()
                    .setPort(8888)
                    .build();

            return new AuthorizationCodeInstalledApp(flow, receiver)
                    .authorize("user");
        } catch (IOException e) {
            throw new CalendarException(e.getMessage());
        }
    }
}
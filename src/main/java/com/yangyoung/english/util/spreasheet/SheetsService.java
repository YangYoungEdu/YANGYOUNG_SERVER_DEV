package com.yangyoung.english.util.spreasheet;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class SheetsService {
    private static final String APPLICATION_NAME = "양영학원 고등부 영어과 관리 프로그램";
    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    //    private static final String SPREADSHEET_ID = "1B17_vfXVo_3kHz3NBYqriR6aTwCywmHNj9Zxjk-Lg4M";
    private static final String SPREADSHEET_ID = "1E6c8cejIokpPKTQ5TpXs19Or_oh9ygFf4gABBoP-6VU";
    private static final String STUDENT_RANGE = "학생!A2:H";
    private static final String LECTURE_RANGE = "강의!A2:J";

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    public static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = SheetsService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Check if tokens directory exists and delete it if it does.
        Path tokenPath = Paths.get(TOKENS_DIRECTORY_PATH);
        if (Files.exists(tokenPath)) {
            Files.walk(tokenPath)
                    .map(Path::toFile)
                    .forEach(File::delete);
            Files.delete(tokenPath);
        }

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).setCallbackPath("/CallBack").build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

//    public static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
//        // Load client secrets.
//        InputStream in = SheetsService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
//        if (in == null) {
//            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
//        }
//        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
//
//        // Build flow and trigger user authorization request.
//        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
//                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
//                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
//                .setAccessType("offline")
//                .build();
//        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8080).setCallbackPath("/CallBack").build();
//        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
//    }

    /**
     * Creates a new Sheets service client.
     *
     * @return Sheets service client.
     * @throws GeneralSecurityException If there is a security issue.
     * @throws IOException              If there is an IO issue.
     */
    private static Sheets createSheetsService() throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Reads the spreadsheet and prints the data.
     *
     * @return List of rows with data from the spreadsheet.
     * @throws IOException              If there is an IO issue.
     * @throws GeneralSecurityException If there is a security issue.
     */
    public static List<List<Object>> readSpreadSheet(String type) throws IOException, GeneralSecurityException {

        String range = switch (type) {
            case "학생" -> STUDENT_RANGE;
            case "강의" -> LECTURE_RANGE;
            default -> throw new IllegalArgumentException("Invalid type: " + type);
        };

        Sheets service = createSheetsService();

        ValueRange response = service.spreadsheets().values()
                .get(SPREADSHEET_ID, range)
                .execute();

        List<List<Object>> values = response.getValues();

        // Ensure each row has a fixed length
        int maxColumns = 11; // Set the desired fixed length
        for (List<Object> row : values) {
            while (row.size() < maxColumns) {
                row.add("");
            }
        }

        return values;
    }
}
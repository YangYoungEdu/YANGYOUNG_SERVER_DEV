package com.yangyoung.english.util.spreasheet;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
public class SheetsService {

    private static final String APPLICATION_NAME = "양영학원 고등부 영어과 관리 프로그램";
    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "/tokens"; // 개발
    //    private static final String TOKENS_DIRECTORY_PATH = "tokens"; // 배포
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private static final String SPREADSHEET_ID = "1P5p3-5WOHTXByVIs-ieR7rTOS2aeyEaRV7tP4u-muAU";
    private static final String STUDENT_RANGE = "학생!A2:G";
    private static final String LECTURE_RANGE = "24년7월강의!A2:I";

    /**
     * Creates an authorized Credential object.
     * <p>
     * //     * @param HTTP_TRANSPORT The network HTTP Transport.
     *
     * @return An authorized Credential object.
     * @throws IOException              If the credentials.json file cannot be found.
     * @throws GeneralSecurityException If there is a security issue.
     */
    public static Sheets getSheetsService() throws IOException, GeneralSecurityException {
        // Load the service account credentials from classpath
        try (InputStream in = SheetsService.class.getResourceAsStream(CREDENTIALS_FILE_PATH)) {
            if (in == null) {
                throw new FileNotFoundException("Resource not found: /credentials.json");
            }
            GoogleCredential credential = GoogleCredential.fromStream(in)
                    .createScoped(SCOPES);

            // Build the Sheets service
            return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        }
    }
//    public static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException, GeneralSecurityException {
//        // Load client secrets.
//        try (InputStream in = SheetsService.class.getResourceAsStream(CREDENTIALS_FILE_PATH)) {
//            if (in == null) {
//                throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
//            }
//            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
//
//            // Path to tokens directory.
////            Path tokenPath = Paths.get(TOKENS_DIRECTORY_PATH);
//            Path tokenPath = Paths.get(System.getProperty("user.home"), "tokens");
//            if (!Files.exists(tokenPath)) {
//                log.info("Creating tokens directory");
//                Files.createDirectories(tokenPath);
//            } else {
//                log.info("Tokens directory exists");
//            }
//            File tokenDirectory = tokenPath.toFile();
//
//            // Build flow and trigger user authorization request.
//            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
//                    HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
//                    .setDataStoreFactory(new FileDataStoreFactory(tokenDirectory))
//                    .setAccessType("offline")
//                    .build();
//
//            return new AuthorizationCodeInstalledApp(
//                    flow, new LocalServerReceiver())
//                    .authorize("user");
//        }
//    }

    /**
     * Creates a new Sheets service client.
     *
     * @return Sheets service client.
     * @throws GeneralSecurityException If there is a security issue.
     * @throws IOException              If there is an IO issue.
     */
//    private static Sheets createSheetsService() throws GeneralSecurityException, IOException {
//        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
//        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
//                .setApplicationName(APPLICATION_NAME)
//                .build();
//    }

    /**
     * Reads the spreadsheet and returns the data.
     *
     * @param type Type of data to read ("학생" or "강의").
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

        Sheets service = getSheetsService();

        ValueRange response = service.spreadsheets().values()
                .get(SPREADSHEET_ID, range)
                .execute();

        List<List<Object>> values = response.getValues();

        // Ensure each row has a fixed length
        int maxColumns = 11; // Set the desired fixed length
        if (values != null) {
            for (List<Object> row : values) {
                while (row.size() < maxColumns) {
                    row.add("");
                }
            }
        }

        return values;
    }
}

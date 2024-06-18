package com.yangyoung.english.util.synology;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class SynologyFileStationService {

    private final RestTemplate restTemplate;

    @Value("${synology.url}")
    private String synologyUrl;
    @Value("${synology.username}")
    private String username;
    @Value("${synology.password}")
    private String password;

    @Autowired
    public SynologyFileStationService(RestTemplateBuilder builder) {
        restTemplate = builder.build();
    }

    public String uploadFile(MultipartFile file, String lecture, LocalDate date) throws IOException {

        String path = "/" + lecture + "/" + date.toString();

        Optional<String> sid = authenticate();
        if (sid.isEmpty()) {
            throw new RuntimeException("Failed to authenticate with Synology");
        }

        String uploadUrl = synologyUrl + "/webapi/entry.cgi?api=SYNO.FileStation.Upload&method=upload&version=2&_sid=" + sid.get();

        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpPost httppost = new HttpPost(uploadUrl);

            File tempFile = File.createTempFile("upload", file.getOriginalFilename());
            file.transferTo(tempFile);

            FileBody fileBody = new FileBody(tempFile, ContentType.DEFAULT_BINARY, file.getOriginalFilename());

            HttpEntity reqEntity = MultipartEntityBuilder.create()
                    .addPart("path", new StringBody(path, ContentType.TEXT_PLAIN))
                    .addPart("create_parents", new StringBody("true", ContentType.TEXT_PLAIN))
                    .addPart("filename", fileBody)
                    .setLaxMode()
                    .build();

            httppost.setEntity(reqEntity);

            try (CloseableHttpResponse response = httpclient.execute(httppost)) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    String responseString = EntityUtils.toString(resEntity);
                    EntityUtils.consume(resEntity);

                    return responseString;
                }
            }

            tempFile.delete();
        }

        return "success";
    }

    public String listFile() {

        Optional<String> sid = authenticate();
        if (sid.isEmpty()) {
            throw new RuntimeException("Failed to authenticate with Synology");
        }

        String listUrl = synologyUrl + "/webapi/entry.cgi?api=SYNO.FileStation.List&method=list&version=2&_sid=" + sid.get();

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(listUrl);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                System.out.println("Response Code: " + response.getStatusLine().getStatusCode());

                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // Print the response content
                    String responseContent = EntityUtils.toString(entity);
                    System.out.println(responseContent);

                    return responseContent;
                }

                EntityUtils.consume(entity);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "success";
    }

    public String searchFile() {

        Optional<String> sid = authenticate();
        if (sid.isEmpty()) {
            throw new RuntimeException("Failed to authenticate with Synology");
        }

        String searchUrl = synologyUrl + "/webapi/entry.cgi?api=SYNO.FileStation.Search&method=start&version=2&_sid=" + sid.get();

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            URIBuilder uriBuilder = new URIBuilder(searchUrl);
            uriBuilder.addParameter("folder_path", "/YangYoung/test");
            URI uri = uriBuilder.build();

            HttpGet request = new HttpGet(uri);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                System.out.println("Response Code: " + response.getStatusLine().getStatusCode());

                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String responseContent = EntityUtils.toString(entity);
                    System.out.println(responseContent);
                }

                EntityUtils.consume(entity);
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        return "success";
    }

    public String getFile() {

        Optional<String> sid = authenticate();
        if (sid.isEmpty()) {
            throw new RuntimeException("Failed to authenticate with Synology");
        }

        String searchUrl = synologyUrl + "/webapi/entry.cgi?api=SYNO.FileStation.List&method=list&version=2&_sid=" + sid.get();

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            URIBuilder uriBuilder = new URIBuilder(searchUrl);
            uriBuilder.addParameter("folder_path", "/YangYoung/test");
//            uriBuilder.addParameter("taskid", "17185419194B5BB52");
            URI uri = uriBuilder.build();

            HttpGet request = new HttpGet(uri);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                System.out.println("Response Code: " + response.getStatusLine().getStatusCode());

                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String responseContent = EntityUtils.toString(entity);
                    System.out.println(responseContent);
                    List<String> fileList = getFileNames(responseContent);
                }

                EntityUtils.consume(entity);
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        return "success";
    }

    public byte[] downloadFile() {
        Optional<String> sid = authenticate();
        if (sid.isEmpty()) {
            throw new RuntimeException("Failed to authenticate with Synology");
        }

        String searchUrl = synologyUrl + "/webapi/entry.cgi?api=SYNO.FileStation.Download&method=download&version=2&_sid=" + sid.get();

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            URIBuilder uriBuilder = new URIBuilder(searchUrl);
            uriBuilder.addParameter("path", "[\"/YangYoung/test/GIT-FLOW.pdf\"]");
            uriBuilder.addParameter("mode", "download");
            URI uri = uriBuilder.build();

            HttpGet request = new HttpGet(uri);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                System.out.println("Response Code: " + response.getStatusLine().getStatusCode());

                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                        entity.writeTo(baos);
                        return baos.toByteArray();
                    }
                }

                EntityUtils.consume(entity);
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        throw new RuntimeException("Failed to download file");
    }

    private Optional<String> authenticate() {

        String loginUrl = synologyUrl + "/webapi/auth.cgi?api=SYNO.API.Auth&version=3&method=login&account=" + username + "&passwd=" + password + "&session=FileStation&format=cookie";
        System.out.println(loginUrl);

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(loginUrl, Map.class);
            Map<String, Object> responseBody = response.getBody();

            if (responseBody != null && Boolean.TRUE.equals(responseBody.get("success"))) {
                Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
                return Optional.ofNullable((String) data.get("sid"));
            }
        } catch (RestClientException e) {
            // Log the exception or handle it accordingly
            System.err.println("Error during authentication: " + e.getMessage());
        }

        return Optional.empty();
    }

    private List<String> getFileNames(String response) throws JsonProcessingException {

        List<String> fileList = new ArrayList<>();

        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode rootNode = objectMapper.readTree(response);

        JsonNode filesNode = rootNode.path("data").path("files");

        for (JsonNode fileNode : filesNode) {
            String fileName = fileNode.path("name").asText();
            System.out.println("file name: " + fileName);
            fileList.add(fileName);
        }

        return fileList;
    }
}

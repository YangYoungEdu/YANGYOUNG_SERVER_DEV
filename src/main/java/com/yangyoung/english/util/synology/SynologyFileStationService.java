package com.yangyoung.english.util.synology;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yangyoung.english.lectureDate.domain.LectureDate;
import com.yangyoung.english.lectureDate.domain.LectureDateRepository;
import com.yangyoung.english.material.dto.request.FileUploadRequest;
import com.yangyoung.english.material.dto.response.MaterialResponse;
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
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@Slf4j
public class SynologyFileStationService {

    private final static String fixedPath = "/YangYoung/고등관/프로그램/";
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB
    private final RestTemplate restTemplate;
    private LectureDateRepository lectureDateRepository;
    @Value("${synology.url}")
    private String synologyUrl;
    @Value("${synology.username}")
    private String username;
    @Value("${synology.password}")
    private String password;

//    private final static String fixedPath = "/YangYoung/";

    @Autowired
    public SynologyFileStationService(RestTemplateBuilder builder, LectureDateRepository lectureDateRepository) {
        restTemplate = builder.build();
        this.lectureDateRepository = lectureDateRepository;
    }

    /*
     * Root directory can not be created, so the path should start with /YangYoung/
     * 408 Error: No such file or directory
     * 418 Error: Illegal name or path
     * ToDo: need to handle the file name with Korean
     * */
    public String uploadFile(FileUploadRequest request) throws IOException {

        Optional<LectureDate> lectureDate = lectureDateRepository.findById(request.getLectureId());
        if (lectureDate.isEmpty()) {
            return null;
        }
        String lectureName = lectureDate.get().getLecture().getName();
        String lectureCode = lectureDate.get().getLecture().getLectureCode();

        List<MultipartFile> fileList = request.getFileList();

        Optional<String> sid = authenticate();
        if (sid.isEmpty()) {
            throw new RuntimeException("Failed to authenticate with Synology");
        }

        String uploadUrl = synologyUrl + "/webapi/entry.cgi?api=SYNO.FileStation.Upload&method=upload&version=2&_sid=" + sid.get();
        String date = request.getDate();
        String folder_path = buildPath(lectureName, lectureCode, date);

        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            for (MultipartFile file : fileList) {
                log.info("File name: {}", file.getSize());
                if (file.getSize() > MAX_FILE_SIZE) {
                    throw new IOException("File size exceeds the maximum limit of " + MAX_FILE_SIZE + " bytes");
                }

                HttpPost httppost = new HttpPost(uploadUrl);

                File tempFile = File.createTempFile("upload", file.getOriginalFilename());
                file.transferTo(tempFile);

                String fileName = file.getOriginalFilename();
                FileBody fileBody = new FileBody(tempFile, ContentType.DEFAULT_BINARY, fileName);

                HttpEntity reqEntity = MultipartEntityBuilder.create()
                        .addPart("path", new StringBody(folder_path, ContentType.create("text/plain", StandardCharsets.UTF_8)))
                        .addPart("create_parents", new StringBody("true", ContentType.create("text/plain", StandardCharsets.UTF_8)))
                        .addPart("filename", fileBody)
                        .setLaxMode()
                        .build();

                httppost.setEntity(reqEntity);

                try (CloseableHttpResponse response = httpclient.execute(httppost)) {
                    HttpEntity resEntity = response.getEntity();
                    if (resEntity != null) {
                        String responseString = EntityUtils.toString(resEntity);
                        EntityUtils.consume(resEntity);

                        System.out.println("Upload response: " + responseString);
                    }
                }

                tempFile.delete();
            }
        }

        return "success";
    }


    /*
     * Get the file list of the lecture on the date
     * */
    public List<MaterialResponse> getFile(Long lectureId, String date) {

        Optional<LectureDate> lectureDate = lectureDateRepository.findById(lectureId);
        if (lectureDate.isEmpty()) {
            return null;
        }
        String lectureName = lectureDate.get().getLecture().getName();
        String lectureCode = lectureDate.get().getLecture().getLectureCode();

        List<String> fileList = new ArrayList<>();

        Optional<String> sid = authenticate();
        if (sid.isEmpty()) {
            throw new RuntimeException("Failed to authenticate with Synology");
        }

        String searchUrl = synologyUrl + "/webapi/entry.cgi?api=SYNO.FileStation.List&method=list&version=2&_sid=" + sid.get();
        String folder_path = buildPath(lectureName, lectureCode, date);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            URIBuilder uriBuilder = new URIBuilder(searchUrl);
            uriBuilder.addParameter("folder_path", folder_path);
            URI uri = uriBuilder.build();

            HttpGet request = new HttpGet(uri);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                System.out.println("Response Code: " + response.getStatusLine().getStatusCode());

                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String responseContent = EntityUtils.toString(entity);
                    System.out.println(responseContent);
                    fileList = getFileNames(responseContent);
                }

                EntityUtils.consume(entity);
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        return fileList.stream()
                .map(fileName -> new MaterialResponse(fileName, date))
                .toList();
    }

    public byte[] downloadFile(Long lectureId, String date, String fileName) {

        Optional<LectureDate> lectureDate = lectureDateRepository.findById(lectureId);
        if (lectureDate.isEmpty()) {
            return null;
        }
        String lectureName = lectureDate.get().getLecture().getName();
        String lectureCode = lectureDate.get().getLecture().getLectureCode();

        Optional<String> sid = authenticate();
        if (sid.isEmpty()) {
            throw new RuntimeException("Failed to authenticate with Synology");
        }

        String path = buildPath(lectureName, lectureCode, date) + "/" + fileName;

        String searchUrl = synologyUrl + "/webapi/entry.cgi?api=SYNO.FileStation.Download&method=download&version=2&_sid=" + sid.get();

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            URIBuilder uriBuilder = new URIBuilder(searchUrl);
            uriBuilder.addParameter("path", path);
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

    public void deleteFile(Long lectureId, String date, String fileName) {
        Optional<String> sid = authenticate();


        if (sid.isEmpty()) {
            throw new RuntimeException("Failed to authenticate with Synology");
        }

        Optional<LectureDate> lectureDate = lectureDateRepository.findById(lectureId);
        if (lectureDate.isEmpty()) {
            return;
        }
        String lectureName = lectureDate.get().getLecture().getName();
        String lectureCode = lectureDate.get().getLecture().getLectureCode();

        String path = buildPath(lectureName, lectureCode, date) + "/" + fileName;

        String deleteUrl = synologyUrl + "/webapi/entry.cgi?api=SYNO.FileStation.Delete&method=start&version=2&_sid=" + sid.get();

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            URIBuilder uriBuilder = new URIBuilder(deleteUrl);
            uriBuilder.addParameter("path", path);
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
    }

    private String buildPath(String lecture, String lectureCode, String date) {
        return fixedPath + lecture + "(" + lectureCode + ")/" + date;
    }
}

package com.yangyoung.english.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

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

    public String uploadFile(MultipartFile file) throws IOException {

        String sid = authenticate();
        if (sid == null) {
            throw new RuntimeException("Failed to authenticate with Synology");
        }

        String uploadUrl = "http://218.158.95.213:5000/webapi/entry.cgi?api=SYNO.FileStation.Upload&method=upload&version=2&_sid=" + sid;

        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpPost httppost = new HttpPost(uploadUrl);

            File tempFile = File.createTempFile("upload", file.getOriginalFilename());
            file.transferTo(tempFile);

            FileBody fileBody = new FileBody(tempFile, ContentType.DEFAULT_BINARY, file.getOriginalFilename());
            System.out.println(fileBody.getFilename());

            HttpEntity reqEntity = MultipartEntityBuilder.create()
                    .addPart("path", new StringBody("/YangYoung/test", ContentType.TEXT_PLAIN))
                    .addPart("create_parents", new StringBody("true", ContentType.TEXT_PLAIN))
                    .addPart("filename", fileBody)
                    .setLaxMode()
                    .build();

            httppost.setEntity(reqEntity);

            System.out.println("Executing request " + httppost.getRequestLine());
            try (CloseableHttpResponse response = httpclient.execute(httppost)) {
                System.out.println("----------------------------------------");
                System.out.println(response.getStatusLine());
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    System.out.println("Response content length: " + resEntity.getContentLength());
                    String responseString = EntityUtils.toString(resEntity);
                    System.out.println(responseString);

                    if (response.getStatusLine().getStatusCode() == 418) {
                        // Handle 418 Illegal name or path error
                        throw new RuntimeException("Illegal name or path: " + responseString);
                    }
                }
                EntityUtils.consume(resEntity);
            }
        }

        return "success";
    }

    private String authenticate() {
        String loginUrl = synologyUrl + "/webapi/auth.cgi?api=SYNO.API.Auth&version=3&method=login&account=" + username + "&passwd=" + password + "&session=FileStation&format=cookie";
        System.out.println(loginUrl);
        ResponseEntity<Map> response = restTemplate.getForEntity(loginUrl, Map.class);

        Map<String, Object> responseBody = response.getBody();
        if (responseBody != null && (boolean) responseBody.get("success")) {
            Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
            return (String) data.get("sid");
        }
        return null;
    }
}

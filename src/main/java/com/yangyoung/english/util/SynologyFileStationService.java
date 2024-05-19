package com.yangyoung.english.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
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

        String uploadUrl = synologyUrl + "/webapi/entry.cgi?api=SYNO.FileStation.Upload&version=2&method=upload&_sid=" + sid;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("folder_path", "/YangYoung/test/");
        body.add("overwrite", "true");
        body.add("file", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(uploadUrl, HttpMethod.POST, requestEntity, String.class);

        return response.getBody();
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

package com.yangyoung.english.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@Slf4j
public class SynologyFileStationService {

    private final RestTemplate restTemplate;
    //    @Value("${synology.url}")
    private String synologyUrl = "http://";
    //    @Value("${synology.username}")
    private String username = "admin";
    //    @Value("${synology.password}")
    private String password = "123456";

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

        // Convert the file's InputStream to a byte array
        byte[] fileData = IOUtils.toByteArray(file.getInputStream());

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("path", "/YangYoung/test/");
        body.add("create_parents", true);
//        body.add("overwrite", true);
        ByteArrayResource byteArrayResource = new ByteArrayResource(fileData) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };
        log.info("byteArrayResource: {}", byteArrayResource);
        body.add("file", byteArrayResource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(uploadUrl, HttpMethod.POST, requestEntity, String.class);

        return response.getBody();
    }

//    public String uploadFile(MultipartFile file) throws IOException {
//        String sid = authenticate();
//        if (sid == null) {
//            throw new RuntimeException("Failed to authenticate with Synology");
//        }
//
//        String uploadUrl = synologyUrl + "/webapi/entry.cgi?api=SYNO.FileStation.Upload&version=2&method=upload&_sid=" + sid;
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//
//        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//        body.add("folder_path", "/YangYoung/test/");
//        body.add("overwrite", "true");
//        body.add("file", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));
//
//        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
//
//        ResponseEntity<String> response = restTemplate.exchange(uploadUrl, HttpMethod.POST, requestEntity, String.class);
//
//        return response.getBody();
//    }

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

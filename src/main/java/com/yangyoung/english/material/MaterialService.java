package com.yangyoung.english.material;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.io.FileSystemResource;

@Service
public class MaterialService {

    private static final String FILE_STATION_API_URL = "http://your_file_station_api_url/upload";
    private static final String FILE_PATH = "path_to_your_file_to_upload";

    public void uploadFileToStation() {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        FileSystemResource file = new FileSystemResource(FILE_PATH);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(FILE_STATION_API_URL, requestEntity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println("File uploaded successfully");
        } else {
            System.out.println("File upload failed");
        }
    }
}

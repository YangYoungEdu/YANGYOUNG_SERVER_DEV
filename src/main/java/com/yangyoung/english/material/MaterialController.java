package com.yangyoung.english.material;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@RestController
public class MaterialController {

//    private static final String FILESTATION_URL = "https://bc4245.tw2.quickconnect.to:443/webapi/entry.cgi";
    private static final String FILESTATION_URL = "https://bc4245.synology.me:5001/webapi/entry.cgi";

    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("api", "SYNO.FileStation.Upload");
        body.add("version", "2");
        body.add("method", "upload");
        body.add("path", "/YangYoung/test/" + file.getOriginalFilename());
        body.add("create_parents", "true");
        body.add("overwrite", "true");
        body.add("file", new FileSystemResource(convert(file)));
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(FILESTATION_URL, requestEntity, String.class);
        System.out.println(response.getBody());
        return response.getBody();
    }

    private File convert(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        convFile.createNewFile();
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }

        return convFile;
    }
}
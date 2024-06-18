package com.yangyoung.english.util.synology;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    private final SynologyFileStationService fileStationService;

    @Autowired
    public FileUploadController(SynologyFileStationService fileStationService) {
        this.fileStationService = fileStationService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("lecture") String lecture, @RequestParam("date") LocalDate date) {
        try {
            String response = fileStationService.uploadFile(file, lecture, date);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("File upload failed: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<String> listFile() {
        try {
            String response = fileStationService.listFile();
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body("File List failed: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<String> searchFile() {
        try {
            String response = fileStationService.searchFile();
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body("File Search failed: " + e.getMessage());
        }
    }

    @GetMapping("/get")
    public ResponseEntity<String> getFile() {
        try {
            String response = fileStationService.getFile();
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body("File Get failed: " + e.getMessage());
        }
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFile() {
        try {
            byte[] fileContent = fileStationService.downloadFile();

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=GIT-FLOW.pdf");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/octet-stream");

            return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(("File Download failed: " + e.getMessage()).getBytes());
        }
    }
}


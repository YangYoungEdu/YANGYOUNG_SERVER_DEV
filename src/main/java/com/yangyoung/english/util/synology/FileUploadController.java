package com.yangyoung.english.util.synology;

import com.yangyoung.english.material.dto.request.FileUploadRequest;
import com.yangyoung.english.material.dto.response.MaterialResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v2/file")
public class FileUploadController {

    private final SynologyFileStationService fileStationService;

    @Autowired
    public FileUploadController(SynologyFileStationService fileStationService) {
        this.fileStationService = fileStationService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @RequestPart("files") List<MultipartFile> fileList,
            @RequestPart("lecture") String lecture,
            @RequestPart("date") String date) {
        try {
            FileUploadRequest request = new FileUploadRequest();
            request.setFileList(fileList);
            request.setLecture(lecture);
            request.setDate(date);

            String response = fileStationService.uploadFile(request);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed: " + e.getMessage());
        }
    }

//    @GetMapping("/get")
//    public ResponseEntity<String> listFile() {
//        try {
//            String response = fileStationService.listFile();
//            return ResponseEntity.ok(response);
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(500).body("File List failed: " + e.getMessage());
//        }
//    }

//    @GetMapping("/search")
//    public ResponseEntity<String> searchFile() {
//        try {
//            String response = fileStationService.searchFile();
//            return ResponseEntity.ok(response);
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(500).body("File Search failed: " + e.getMessage());
//        }
//    }

    @GetMapping("")
    public ResponseEntity<List<MaterialResponse>> getFile(@RequestParam String lecture, @RequestParam String date) {
        try {
            List<MaterialResponse> response = fileStationService.getFile(lecture, date);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFile(@RequestParam String lecture, @RequestParam String date, @RequestParam String fileName) {
        try {
            byte[] fileContent = fileStationService.downloadFile(lecture, date, fileName);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=GIT-FLOW.pdf");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/octet-stream");

            return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(("File Download failed: " + e.getMessage()).getBytes());
        }
    }

    @GetMapping("/delete")
    public ResponseEntity<Void> deleteFile(@RequestParam String lecture, @RequestParam String date, @RequestParam String fileName) {
        try {
            fileStationService.deleteFile(lecture, date, fileName);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).build();
        }
    }
}


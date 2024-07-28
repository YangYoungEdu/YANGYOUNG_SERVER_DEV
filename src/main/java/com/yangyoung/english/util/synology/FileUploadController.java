package com.yangyoung.english.util.synology;

import com.yangyoung.english.material.dto.request.FileUploadRequest;
import com.yangyoung.english.material.dto.response.MaterialResponse;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@RequestMapping("/api/v2/file")
public class FileUploadController {

    private final SynologyFileStationService fileStationService;

//    @PostMapping("")
//    public ResponseEntity<String> uploadFile(
//            @RequestPart("files") List<MultipartFile> fileList,
//            @RequestPart("lecture") String lecture,
//            @RequestPart("date") String date,
//            @RequestHeader(value = "Authorization") String token) {
//        try {
//            FileUploadRequest request = new FileUploadRequest();
//            request.setFileList(fileList);
//            request.setLecture(lecture);
//            request.setDate(date);
//
//            String response = fileStationService.uploadFile(request);
//            return ResponseEntity.ok(response);
//        } catch (IOException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed: " + e.getMessage());
//        }
//    }

    @PostMapping("")
    public ResponseEntity<String> uploadFile(@ModelAttribute FileUploadRequest request,
                                             @RequestHeader(value = "Authorization") String token) {
        try {
            String response = fileStationService.uploadFile(request);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed: " + e.getMessage());
        }
    }

    @GetMapping("")
    public ResponseEntity<List<MaterialResponse>> getFile(@RequestParam Long lectureId,
                                                          @RequestParam String date,
                                                          @RequestHeader(value = "Authorization") String token) {
        try {
            List<MaterialResponse> response = fileStationService.getFile(lectureId, date);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFile(@RequestParam Long lectureId,
                                               @RequestParam String date,
                                               @RequestParam String fileName,
                                               @RequestHeader(value = "Authorization") String token) {
        try {
            byte[] fileContent = fileStationService.downloadFile(lectureId, date, fileName);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=GIT-FLOW.pdf");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/octet-stream");

            return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(("File Download failed: " + e.getMessage()).getBytes());
        }
    }

    @GetMapping("/delete")
    public ResponseEntity<Void> deleteFile(@RequestParam Long lectureId,
                                           @RequestParam String date,
                                           @RequestParam String fileName,
                                           @RequestHeader(value = "Authorization") String token) {
        try {
            fileStationService.deleteFile(lectureId, date, fileName);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).build();
        }
    }
}


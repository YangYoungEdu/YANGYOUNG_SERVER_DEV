package com.yangyoung.english.material.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FileUploadRequest {

    private List<MultipartFile> fileList;

    private String lecture;

    private String date;
}

package com.yangyoung.english.student.dto.request;

import com.yangyoung.english.util.SheetType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentAddByExcelRequest {

    private MultipartFile file;

    private SheetType sheetType;
}

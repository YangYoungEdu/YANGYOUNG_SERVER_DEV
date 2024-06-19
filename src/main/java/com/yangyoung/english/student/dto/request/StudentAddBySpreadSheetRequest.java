package com.yangyoung.english.student.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StudentAddBySpreadSheetRequest {

    private String link;

    private String sheetName;
}

package com.yangyoung.english.student.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StudentAddByExcelResponse {

    private List<StudentResponse> newStudentList;

    private Integer newStudentCount;
}

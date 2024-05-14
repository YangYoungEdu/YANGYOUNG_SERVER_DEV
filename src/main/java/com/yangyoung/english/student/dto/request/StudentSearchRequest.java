package com.yangyoung.english.student.dto.request;

import com.yangyoung.english.student.domain.Grade;
import lombok.Getter;

import java.util.List;

@Getter
public class StudentSearchRequest {

    private List<String> nameList;

    private List<String> schoolList;

    private List<Grade> gradeList;

    private int page;

    private int size;
}

package com.yangyoung.english.lecture.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class AddLectureBySheetRequest {

    private List<String> presetList;

    private List<String> studentList;

    private List<String> excludeStudentList;

    private List<String> addedStudentList;
}

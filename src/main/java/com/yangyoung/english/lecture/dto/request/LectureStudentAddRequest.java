package com.yangyoung.english.lecture.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class LectureStudentAddRequest {

    private Long lectureId;

    private List<Long> studentIdList;
}

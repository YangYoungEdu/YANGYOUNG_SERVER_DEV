package com.yangyoung.english.lecture.dto.response;

import com.yangyoung.english.lecture.domain.Lecture;
import com.yangyoung.english.lectureDay.domain.LectureDay;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LectureDetailResponse {

    private Long id;

    private String name;

    private String teacher;

    private String room;

    private String startTime;

    private String endTime;

    private List<String> dayList;

    private List<String> dateList;
}

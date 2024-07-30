package com.yangyoung.english.lecture.dto.request;

import com.yangyoung.english.lecture.domain.Lecture;
import com.yangyoung.english.lecture.domain.LectureType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class LectureUpdateRequest {

    private Boolean isAllUpdate;

    private Long id;

    private String name;

    private String lectureType;

    private String teacher;

    private String room;

    private LocalTime startTime;

    private LocalTime endTime;

    private List<LocalDate> lectureDates;


    public Lecture toEntity(String lectureCode) {
        return Lecture.builder()
                .lectureCode(lectureCode)
                .name(name)
                .teacher(teacher)
                .room(room)
                .startTime(startTime)
                .endTime(endTime)
                .isRepeated(false)
                .lectureType(LectureType.getLectureTypeByDescription(lectureType))
                .build();
    }
}

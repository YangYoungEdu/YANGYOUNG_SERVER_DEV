package com.yangyoung.english.lecture.dto.request;

import com.yangyoung.english.lecture.domain.Lecture;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class AddLectureByFormRequest {

    private String name;

    private String teacher;

    private String room;

    private LocalTime startTime;

    private LocalTime endTime;

    private List<DayOfWeek> lectureDayList;

    private List<LocalDate> lectureDateList;

    private List<Long> studentList;

    public Lecture toEntity() {
        return Lecture.builder()
                .name(name)
                .teacher(teacher)
                .room(room)
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }
}

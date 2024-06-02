package com.yangyoung.english.lecture.dto.response;

import com.yangyoung.english.lecture.domain.Lecture;
import com.yangyoung.english.lectureDate.domain.LectureDate;
import com.yangyoung.english.lectureDay.domain.LectureDay;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LectureBriefResponse {

    private Long id;

    private String name;

    private String teacher;

    private String room;

    private String startTime;

    private String endTime;

    private List<String> lectureDateList;

    private List<String> lectureDayList;

    private boolean isFinished;

    public LectureBriefResponse(Lecture lecture) {
        this.id = lecture.getId();
        this.name = lecture.getName();
        this.teacher = lecture.getTeacher();
        this.room = lecture.getRoom();
        this.startTime = lecture.getStartTime().toString();
        this.endTime = lecture.getEndTime().toString();
        this.isFinished = lecture.isFinished();
        this.lectureDateList = lecture.getLectureDateList().stream()
                .map(LectureDate::getLectureDate)
                .map(LocalDate::toString)
                .toList();
        this.lectureDayList = lecture.getLectureDayList().stream()
                .map(LectureDay::getLectureDay)
                .map(DayOfWeek::name)
                .toList();
    }
}

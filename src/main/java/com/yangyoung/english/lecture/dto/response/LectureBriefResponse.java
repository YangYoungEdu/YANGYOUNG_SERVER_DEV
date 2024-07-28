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
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LectureBriefResponse {

    private Long id;

    private String lectureCode;

    private String name;

    private String teacher;

    private String room;

    private LectureTime startTime;

    private LectureTime endTime;

    private String lectureDate;

    private String lectureDay;

    private boolean isFinished;

    private boolean isRepeated;

    public LectureBriefResponse(LectureDate lectureDate) {
        this.id = lectureDate.getId();
        this.lectureCode = lectureDate.getLecture().getLectureCode();
        this.lectureDate = lectureDate.getLectureDate().toString();
        this.isFinished = lectureDate.getLecture().isFinished();
        this.isRepeated = lectureDate.getLecture().isRepeated();
        this.name = lectureDate.getLecture().getName();
        this.teacher = lectureDate.getLecture().getTeacher();
        this.room = lectureDate.getLecture().getRoom();
        this.startTime = new LectureTime(lectureDate.getLecture().getStartTime().getHour(), lectureDate.getLecture().getStartTime().getMinute());
        this.endTime = new LectureTime(lectureDate.getLecture().getEndTime().getHour(), lectureDate.getLecture().getEndTime().getMinute());
    }

    public LectureBriefResponse(Lecture lecture) {
        this.id = lecture.getId();
        this.lectureCode = lecture.getLectureCode();
        this.name = lecture.getName();
        this.teacher = lecture.getTeacher();
        this.room = lecture.getRoom();
        this.startTime = new LectureTime(lecture.getStartTime().getHour(), lecture.getStartTime().getMinute());
        this.endTime = new LectureTime(lecture.getEndTime().getHour(), lecture.getEndTime().getMinute());
        this.isFinished = lecture.isFinished();
        this.isRepeated = lecture.isRepeated();
    }
}

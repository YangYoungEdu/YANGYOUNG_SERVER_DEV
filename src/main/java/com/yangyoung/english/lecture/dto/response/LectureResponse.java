package com.yangyoung.english.lecture.dto.response;

import com.yangyoung.english.lecture.domain.Lecture;
import com.yangyoung.english.lectureDay.domain.LectureDay;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LectureResponse {

    private Long id;

    private Long lecSeq;

    private String name;

    private String teacher;

    private String room;

    private String startTime;

    private String endTime;

    private List<String> dayList;

    private List<String> dateList;

    public LectureResponse(Lecture lecture) {
        this.id = lecture.getId();
        this.lecSeq = lecture.getSeq();
        this.name = lecture.getName();
        this.teacher = lecture.getTeacher();
        this.room = lecture.getRoom();
        this.startTime = lecture.getStartTime().toString();
        this.endTime = lecture.getEndTime().toString();
        this.dayList = lecture.getLectureDayList().stream()
                .map(LectureDay::getLectureDay)
                .toList();
        this.dateList = lecture.getLectureDateList().stream()
                .map(lectureDate -> lectureDate.getLectureDate().toString())
                .toList();
    }
}

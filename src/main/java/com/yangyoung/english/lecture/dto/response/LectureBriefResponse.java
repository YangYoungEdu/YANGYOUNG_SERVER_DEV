package com.yangyoung.english.lecture.dto.response;

import com.yangyoung.english.lecture.domain.Lecture;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    private boolean isFinished;

    public LectureBriefResponse(Lecture lecture) {
        this.id = lecture.getId();
        this.name = lecture.getName();
        this.teacher = lecture.getTeacher();
        this.room = lecture.getRoom();
        this.startTime = lecture.getStartTime().toString();
        this.endTime = lecture.getEndTime().toString();
        this.isFinished = lecture.isFinished();
    }
}

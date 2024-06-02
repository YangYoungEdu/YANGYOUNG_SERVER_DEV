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

    private Long lecSeq;

    private String name;

    private String teacher;

    private String room;

    private String startTime;

    private String endTime;

    private List<String> dayList;

    private List<String> dateList;

    public LectureDetailResponse(Lecture lecture) {
        this.id = lecture.getId();
        this.lecSeq = lecture.getSeq();
        this.name = lecture.getName();
        this.teacher = lecture.getTeacher();
        this.room = lecture.getRoom();
        this.startTime = lecture.getStartTime().toString();
        this.endTime = lecture.getEndTime().toString();
        if (lecture.getLectureDayList() != null) {
            this.dayList = lecture.getLectureDayList().stream()
                    .map(lectureDay -> LectureDay.getLectureDay(lectureDay.getLectureDay()))
                    .collect(Collectors.toList());
        }
        if (lecture.getLectureDateList() != null) {
            this.dateList = lecture.getLectureDateList().stream()
                    .map(lectureDate -> lectureDate.getLectureDate().toString())
                    .collect(Collectors.toList());
        }
    }
}

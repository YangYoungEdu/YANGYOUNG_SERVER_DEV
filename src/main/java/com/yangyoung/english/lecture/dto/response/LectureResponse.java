package com.yangyoung.english.lecture.dto.response;

import com.yangyoung.english.lecture.domain.Lecture;
import com.yangyoung.english.lectureDay.domain.LectureDay;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.util.List;
import java.util.stream.Collectors;

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

    private boolean isFinished;

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
        this.isFinished = lecture.isFinished();
        if (lecture.getLectureDayList() != null) {
            for (int i = 0; i < lecture.getLectureDayList().size(); i++) {
                DayOfWeek lectureDay = lecture.getLectureDayList().get(i).getLectureDay();
                this.dayList.add(LectureDay.getLectureDay(lectureDay));
            }
        }
        if (lecture.getLectureDateList() != null) {
            this.dateList = lecture.getLectureDateList().stream()
                    .map(lectureDate -> lectureDate.getLectureDate().toString())
                    .toList();
        }
    }
}

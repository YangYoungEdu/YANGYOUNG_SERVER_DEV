package com.yangyoung.english.lecture.dto.response;

import com.yangyoung.english.lecture.domain.Lecture;
import com.yangyoung.english.lectureDate.domain.LectureDate;
import com.yangyoung.english.lectureDay.domain.LectureDay;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LectureResponse {

    private Long id;

    private String lectureCode;

    private String name;

    private String lectureType;

    private String teacher;

    private String room;

    private LectureTime startTime;

    private LectureTime endTime;

    private String lectureDate;

    private List<String> allLectureDate = new ArrayList<>();

    private boolean isFinished;

    private boolean isRepeated;

    public LectureResponse(Lecture lecture) {
        this.id = lecture.getId();
        this.lectureCode = lecture.getLectureCode();
        this.name = lecture.getName();
        this.lectureType = lecture.getLectureType().getDescription();
        this.teacher = lecture.getTeacher();
        this.room = lecture.getRoom();
        this.startTime = new LectureTime(lecture.getStartTime().getHour(), lecture.getStartTime().getMinute());
        this.endTime = new LectureTime(lecture.getEndTime().getHour(), lecture.getEndTime().getMinute());
        this.isFinished = lecture.isFinished();
        this.isRepeated = lecture.isRepeated();
        if (lecture.getLectureDateList() != null) {
            this.lectureDate = lecture.getLectureDateList().get(0).getLectureDate().toString();
            this.allLectureDate = lecture.getLectureDateList()
                    .stream()
                    .map(lectureDate -> lectureDate.getLectureDate().toString())
                    .collect(Collectors.toList());
        }
    }

    public LectureResponse(LectureDate lectureDate) {
        this.id = lectureDate.getId();
        this.lectureCode = lectureDate.getLecture().getLectureCode();
        this.lectureDate = lectureDate.getLectureDate().toString();
        if (lectureDate.getLecture().getLectureDateList() != null) {
            this.allLectureDate = lectureDate.getLecture().getLectureDateList()
                    .stream()
                    .map(lectureDateObj -> lectureDateObj.getLectureDate().toString()) // LectureDate 객체의 getLectureDate() 메소드 호출 후 toString() 호출
                    .collect(Collectors.toList());
        }
        this.isFinished = lectureDate.getLecture().isFinished();
        this.lectureType = lectureDate.getLecture().getLectureType().getDescription();
        this.isRepeated = lectureDate.getLecture().isRepeated();
        this.name = lectureDate.getLecture().getName();
        this.teacher = lectureDate.getLecture().getTeacher();
        this.room = lectureDate.getLecture().getRoom();
        this.startTime = new LectureTime(lectureDate.getLecture().getStartTime().getHour(), lectureDate.getLecture().getStartTime().getMinute());
        this.endTime = new LectureTime(lectureDate.getLecture().getEndTime().getHour(), lectureDate.getLecture().getEndTime().getMinute());
    }
}

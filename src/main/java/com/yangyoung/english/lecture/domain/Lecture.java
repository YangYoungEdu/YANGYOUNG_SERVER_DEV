package com.yangyoung.english.lecture.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.yangyoung.english.attendance.domain.Attendance;
import com.yangyoung.english.lectureDate.domain.LectureDate;
import com.yangyoung.english.lectureDay.domain.LectureDay;
import com.yangyoung.english.lectureTask.domain.LectureTask;
import com.yangyoung.english.studentLecture.domain.StudentLecture;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private Long seq;

    @Enumerated(EnumType.STRING)
    private LectureType lectureType;

    private String name;

    private String teacher;

    private String room;

    private LocalTime startTime;

    private LocalTime endTime;

    private boolean isFinished;

    @OneToMany(mappedBy = "lecture", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonBackReference
    private List<LectureDay> lectureDayList;

    @OneToMany(mappedBy = "lecture", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonBackReference
    private List<LectureDate> lectureDateList;

    @OneToMany(mappedBy = "lecture", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonBackReference
    private List<StudentLecture> studentLectureList;

    @OneToMany(mappedBy = "lecture", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonBackReference
    private List<LectureTask> lectureTaskList;

    @OneToMany(mappedBy = "lecture", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Attendance> attendanceList;


    @Builder
    public Lecture(Long id, LectureType lectureType, String name, String teacher, String room, LocalTime startTime, LocalTime endTime) {
        this.id = id;
        this.seq = id;
        this.lectureType = lectureType;
        this.name = name;
        this.teacher = teacher;
        this.room = room;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isFinished = false;
    }

    public void update(String name, String teacher, String room, LocalTime startTime, LocalTime endTime) {
        if (!name.isBlank()) {
            this.name = name;
        }
        if (!teacher.isBlank()) {
            this.teacher = teacher;
        }
        if (!room.isBlank()) {
            this.room = room;
        }
        if (startTime != null) {
            this.startTime = startTime;
        }
        if (endTime != null) {
            this.endTime = endTime;
        }
    }

    public void updateSeq(Long seq) {
        this.seq = seq;
    }

    public void updateIsFinished() {
        this.isFinished = true;
    }
}

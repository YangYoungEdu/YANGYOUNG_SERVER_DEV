package com.yangyoung.english.lecture.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.yangyoung.english.attendance.domain.Attendance;
import com.yangyoung.english.configuration.BaseEntity;
import com.yangyoung.english.lectureDate.domain.LectureDate;
import com.yangyoung.english.lectureDay.domain.LectureDay;
import com.yangyoung.english.lectureSection.domain.LectureSection;
import com.yangyoung.english.lectureTask.domain.LectureTask;
import com.yangyoung.english.section.domain.Section;
import com.yangyoung.english.student.domain.Grade;
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
public class Lecture extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String lectureCode;

    @Enumerated(EnumType.STRING)
    private LectureType lectureType;

    private String name;

    private String teacher;

    private String room;

    private LocalTime startTime;

    private LocalTime endTime;

    private boolean isFinished;

    private boolean isRepeated;

    @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<LectureDay> lectureDayList;

    @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<LectureDate> lectureDateList;

    @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<StudentLecture> studentLectureList;

    @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<LectureTask> lectureTaskList;

    @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<Attendance> attendanceList;

    @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<LectureSection> lectureSectionList;


    @Builder
    public Lecture(Long id, String name, String teacher, String room, LocalTime startTime, LocalTime endTime, String lectureCode, boolean isRepeated, LectureType lectureType) {
        this.id = id;
        this.name = name;
        this.teacher = teacher;
        this.room = room;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isFinished = false;
        this.lectureCode = lectureCode;
        this.isRepeated = isRepeated;
        this.lectureType = lectureType;
    }

    @Builder
    public Lecture(String name, String teacher, String room, LocalTime startTime, LocalTime endTime, String lectureCode, boolean isRepeated, LectureType lectureType) {
        this.name = name;
        this.teacher = teacher;
        this.room = room;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isFinished = false;
        this.lectureCode = lectureCode;
        this.isRepeated = isRepeated;
        this.lectureType = lectureType;
    }


    public void update(String name, String teacher, String room, LocalTime startTime, LocalTime endTime, String lectureType) {
        if (name != null) {
            this.name = name;
        }
        if (teacher != null) {
            this.teacher = teacher;
        }
        if (room != null) {
            this.room = room;
        }
        if (startTime != null) {
            this.startTime = startTime;
        }
        if (endTime != null) {
            this.endTime = endTime;
        }
        if (lectureType != null) {
            this.lectureType = LectureType.getLectureTypeByDescription(lectureType);
        }
    }

    public void updateIsFinished() {
        this.isFinished = true;
    }
}

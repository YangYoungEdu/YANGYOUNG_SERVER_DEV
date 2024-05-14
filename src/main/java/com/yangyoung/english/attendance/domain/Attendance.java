package com.yangyoung.english.attendance.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.yangyoung.english.lecture.domain.Lecture;
import com.yangyoung.english.student.domain.Student;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private AttendanceType attendanceType;

    private String note;

    private LocalDateTime attendedDateTime;

    @ManyToOne()
    @JoinColumn(name = "student_id")
    @JsonManagedReference
    private Student student;

    @ManyToOne()
    @JoinColumn(name = "lecture_id")
    @JsonManagedReference
    private Lecture lecture;

    @Builder
    public Attendance(AttendanceType attendanceType, LocalDateTime attendedDateTime, Student student, Lecture lecture) {
        this.attendanceType = attendanceType;
        this.note = "";
        this.attendedDateTime = attendedDateTime;
        this.student = student;
        this.lecture = lecture;
    }


}

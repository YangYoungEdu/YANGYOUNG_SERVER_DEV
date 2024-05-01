package com.yangyoung.english.attendance.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.yangyoung.english.student.domain.Student;
import jakarta.persistence.*;
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
}

package com.yangyoung.english.studentLecture.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.yangyoung.english.lecture.domain.Lecture;
import com.yangyoung.english.student.domain.Student;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudentLecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "student_id")
    @JsonManagedReference
    private Student student;

    @ManyToOne()
    @JoinColumn(name = "lecture_id")
    @JsonManagedReference
    private Lecture lecture;

    @Builder
    public StudentLecture(Student student, Lecture lecture) {
        this.student = student;
        this.lecture = lecture;
    }
}

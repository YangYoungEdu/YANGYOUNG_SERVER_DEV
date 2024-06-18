package com.yangyoung.english.exam.domain;

import com.yangyoung.english.school.domain.School;
import com.yangyoung.english.student.domain.Grade;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Grade grade;

    private String scope;

    private boolean isFinished;

    @ManyToOne()
    @JoinColumn(name = "school_id")
    private School school;
}

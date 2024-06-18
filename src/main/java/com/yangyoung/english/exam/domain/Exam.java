package com.yangyoung.english.exam.domain;

import com.yangyoung.english.configuration.BaseEntity;
import com.yangyoung.english.school.domain.School;
import com.yangyoung.english.student.domain.Grade;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Exam extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Grade grade;

    private LocalDate startDate;

    private LocalDate endDate;

    private String scope;

    private boolean isFinished;

    @ManyToOne()
    @JoinColumn(name = "school_id")
    private School school;
}

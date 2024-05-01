package com.yangyoung.english.lectureDate.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.yangyoung.english.lecture.domain.Lecture;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
public class LectureDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate lectureDate;

    @ManyToOne()
    @JoinColumn(name = "lecture_id")
    @JsonManagedReference
    private Lecture lecture;

    @Builder
    public LectureDate(LocalDate lectureDate, Lecture lecture) {
        this.lectureDate = lectureDate;
        this.lecture = lecture;
    }
}

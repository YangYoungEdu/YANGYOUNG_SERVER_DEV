package com.yangyoung.english.lectureSection.domain;

import com.yangyoung.english.lecture.domain.Lecture;
import com.yangyoung.english.section.domain.Section;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LectureSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    @ManyToOne
    @JoinColumn(name = "section_id")
    private Section section;

    @Builder
    public LectureSection(Lecture lecture, Section section) {
        this.lecture = lecture;
        this.section = section;
    }
}

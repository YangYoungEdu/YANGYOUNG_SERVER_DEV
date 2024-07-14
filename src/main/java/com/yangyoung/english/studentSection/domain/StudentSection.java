package com.yangyoung.english.studentSection.domain;

import com.yangyoung.english.section.domain.Section;
import com.yangyoung.english.student.domain.Student;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudentSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne()
    @JoinColumn(name = "section_id")
    private Section section;

    @Builder
    public StudentSection(Student student, Section section) {
        this.student = student;
        this.section = section;
    }
}

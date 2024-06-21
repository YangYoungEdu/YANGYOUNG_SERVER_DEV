package com.yangyoung.english.student.dto.request;

import com.yangyoung.english.school.domain.School;
import com.yangyoung.english.section.domain.Section;
import com.yangyoung.english.student.domain.Grade;
import com.yangyoung.english.student.domain.Student;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StudentAddRequest {

    private Long id;

    private String name;

    private String school;

    private String grade;

    private String section;

    private String studentPhoneNumber;

    private String parentPhoneNumber;

    public Student toEntity(School school, Section section) {
        return Student.builder()
                .id(id)
                .name(name)
                .school(school)
                .section(section)
                .grade(Grade.getGradeName(grade))
                .studentPhoneNumber(studentPhoneNumber)
                .parentPhoneNumber(parentPhoneNumber)
                .build();
    }
}

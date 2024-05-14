package com.yangyoung.english.student.dto.response;

import com.yangyoung.english.student.domain.Student;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StudentBriefResponse {

    private Long id;

    private String name;

    private String school;

    private String grade;

    public StudentBriefResponse(Student student) {
        this.id = student.getId();
        this.name = student.getName();
        this.school = student.getSchool();
        this.grade = student.getGrade().getGradeName();
    }
}

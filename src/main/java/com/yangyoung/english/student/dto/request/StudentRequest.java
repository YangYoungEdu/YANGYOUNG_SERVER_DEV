package com.yangyoung.english.student.dto.request;

import com.yangyoung.english.student.domain.Grade;
import com.yangyoung.english.student.domain.Student;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StudentRequest {

    private Long id;

    private String name;

    private String school;

    private Grade grade;

    private String studentPhoneNumber;

    private String parentPhoneNumber;

    public Student toEntity() {
        return Student.builder()
                .id(id)
                .name(name)
                .school(school)
                .grade(grade)
                .studentPhoneNumber(studentPhoneNumber)
                .parentPhoneNumber(parentPhoneNumber)
                .build();
    }
}

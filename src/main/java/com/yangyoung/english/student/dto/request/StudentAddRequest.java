package com.yangyoung.english.student.dto.request;

import com.yangyoung.english.school.domain.School;
import com.yangyoung.english.section.domain.Section;
import com.yangyoung.english.student.domain.Grade;
import com.yangyoung.english.student.domain.Student;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

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

    public static StudentAddRequest of(List<Object> objectList) {
        return new StudentAddRequest(
                Long.parseLong(objectList.get(0).toString()),
                objectList.get(1).toString(),
                objectList.get(2).toString(),
                objectList.get(3).toString(),
                objectList.get(4).toString(),
                objectList.get(5).toString(),
                objectList.get(6).toString()
        );
    }

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

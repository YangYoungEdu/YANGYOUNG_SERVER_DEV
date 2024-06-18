package com.yangyoung.english.student.dto.response;

import com.yangyoung.english.student.domain.Student;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponse {

    private Long id;

    private String name;

    private String school;

    private String grade;

    private String studentPhoneNumber;

    private String parentPhoneNumber;

    public StudentResponse(Student student) {
        this.id = student.getId();
        this.name = student.getName();
        this.school = student.getSchool().getName();
        this.grade = student.getGrade().getGradeName();
        this.studentPhoneNumber = student.getStudentPhoneNumber();
        this.parentPhoneNumber = student.getParentPhoneNumber();
    }
}

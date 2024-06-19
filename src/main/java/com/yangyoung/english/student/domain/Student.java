package com.yangyoung.english.student.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.yangyoung.english.attendance.domain.Attendance;
import com.yangyoung.english.configuration.BaseEntity;
import com.yangyoung.english.school.domain.School;
import com.yangyoung.english.studentLecture.domain.StudentLecture;
import com.yangyoung.english.studentTask.domain.StudentTask;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Student extends BaseEntity {

    @Id
    private Long id;

    private Long seq;

    private boolean isEnrolled;

    private String name;

    @ManyToOne()
    @JoinColumn(name = "school_id")
    @JsonManagedReference
    private School school;

    @Enumerated(EnumType.STRING)
    private Grade grade;

    private String studentPhoneNumber;

    private String parentPhoneNumber;

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonBackReference
    private List<StudentLecture> studentLectureList;

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonBackReference
    private List<StudentTask> studentTaskList;

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Attendance> attendanceList;

    @Builder
    public Student(Long id, String name, School school, Grade grade, String studentPhoneNumber, String parentPhoneNumber) {
        this.id = id;
        this.seq = id;
        this.name = name;
        this.school = school;
        this.grade = grade;
        this.studentPhoneNumber = studentPhoneNumber;
        this.parentPhoneNumber = parentPhoneNumber;
        this.isEnrolled = true;
    }

    public void update(String name, School school, String grade, String studentPhoneNumber, String parentPhoneNumber) {
        if (!name.isBlank()) {
            this.name = name;
        }
        if (school != null) {
            this.school = school;
        }
        if (grade != null) {
            this.grade = Grade.getGradeName(grade);
        }
        if (!studentPhoneNumber.isBlank()) {
            this.studentPhoneNumber = studentPhoneNumber;
        }
        if (!parentPhoneNumber.isBlank()) {
            this.parentPhoneNumber = parentPhoneNumber;
        }
    }

    public void updateSequence(Long id) {
        this.id = id;
    }

    public void updateEnrollStatus(boolean isEnrolled) {
        this.isEnrolled = isEnrolled;
    }
}

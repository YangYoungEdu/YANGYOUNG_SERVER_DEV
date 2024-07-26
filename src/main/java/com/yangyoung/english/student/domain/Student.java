package com.yangyoung.english.student.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.yangyoung.english.appUser.domain.AppUser;
import com.yangyoung.english.attendance.domain.Attendance;
import com.yangyoung.english.configuration.BaseEntity;
import com.yangyoung.english.school.domain.School;
import com.yangyoung.english.school.domain.School;
import com.yangyoung.english.section.domain.Section;
import com.yangyoung.english.studentLecture.domain.StudentLecture;
import com.yangyoung.english.studentSection.domain.StudentSection;
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

    @OneToOne()
    @JoinColumn(name = "app_user_id") // Customizes the column name for the join column
    private AppUser appUser;

    @Id
    private Long id;

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

    private Boolean isLectureRegistered;

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonBackReference
    private List<StudentSection> studentSectionList;

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
        this.name = name;
        this.school = school;
        this.grade = grade;
        this.studentPhoneNumber = studentPhoneNumber;
        this.parentPhoneNumber = parentPhoneNumber;
        this.isEnrolled = true;
        this.isLectureRegistered = false;
    }

    @Builder
    public Student(List<Object> studentData, School school) {
        this.id = Long.parseLong(studentData.get(0).toString());
        this.name = studentData.get(1).toString();
        this.school = school;
        this.grade = Grade.getGradeName(studentData.get(3).toString());
        this.studentPhoneNumber = (String) studentData.get(4);
        this.parentPhoneNumber = (String) studentData.get(5);
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

    public void update(List<Object> updateData) {
        if (updateData.get(0) != null) {
            this.name = (String) updateData.get(0);
        }
        if (updateData.get(1) != null) {
            this.school = (School) updateData.get(1);
        }
        if (updateData.get(2) != null) {
            this.grade = (Grade) updateData.get(2);
        }
        if (updateData.get(3) != null) {
            this.studentPhoneNumber = (String) updateData.get(3);
        }
        if (updateData.get(4) != null) {
            this.parentPhoneNumber = (String) updateData.get(4);
        }
    }

    public void updateEnrollStatus(boolean isEnrolled) {
        this.isEnrolled = isEnrolled;
    }

    public void updateIsLectureRegistered(Boolean isLectureRegistered) {
        this.isLectureRegistered = isLectureRegistered;
    }
}

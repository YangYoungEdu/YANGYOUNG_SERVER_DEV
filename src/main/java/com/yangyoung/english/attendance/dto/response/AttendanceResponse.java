package com.yangyoung.english.attendance.dto.response;

import com.yangyoung.english.attendance.domain.Attendance;
import com.yangyoung.english.student.domain.Student;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceResponse {

    private Long id;

    private String name;

    private String studentPhoneNumber;

    private String parentPhoneNumber;

    private String attendanceType;

    private LocalDateTime attendedDateTime;

    private String note;

    // 출석 정보 있는 경우
    public AttendanceResponse(Student student, Attendance attendance) {
        this.id = attendance.getId();
        this.name = student.getName();
        this.studentPhoneNumber = student.getStudentPhoneNumber();
        this.parentPhoneNumber = student.getParentPhoneNumber();
        this.attendanceType = attendance.getAttendanceType().name();
        this.attendedDateTime = attendance.getAttendedDateTime();
        this.note = attendance.getNote();
    }

    // 출석 정보 없는 경우
    public AttendanceResponse(Student student) {
        this.id = null;
        this.name = student.getName();
        this.studentPhoneNumber = student.getStudentPhoneNumber();
        this.parentPhoneNumber = student.getParentPhoneNumber();
        this.attendanceType = null;
        this.attendedDateTime = null;
        this.note = null;
    }
}

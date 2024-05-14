package com.yangyoung.english.attendance.domain;

import com.yangyoung.english.student.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    boolean existsByStudentAndAttendedDateTimeBetween(Student student, LocalDateTime startDateTime, LocalDateTime endDateTime);

}

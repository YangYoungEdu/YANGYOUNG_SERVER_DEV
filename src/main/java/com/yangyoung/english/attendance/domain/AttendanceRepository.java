package com.yangyoung.english.attendance.domain;

import com.yangyoung.english.lecture.domain.Lecture;
import com.yangyoung.english.student.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    boolean existsByStudentAndAttendedDateTimeBetween(Student student, LocalDateTime startDateTime, LocalDateTime endDateTime);

    Optional<Attendance> findByStudentAndAttendedDateTimeBetween(Student student, LocalDateTime startDateTime, LocalDateTime endDateTime);

    List<Attendance> findByLectureId(Long lectureId);

    List<Attendance> findByLecture(Lecture lecture);

    Optional<Attendance> findByStudentId(Long studentId);

    List<Attendance> findByStudentIdInAndAttendedDateTimeBetween(List<Long> studentIds, LocalDateTime startDateTime, LocalDateTime endDateTime);
}

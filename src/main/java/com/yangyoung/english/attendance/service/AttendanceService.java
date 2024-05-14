package com.yangyoung.english.attendance.service;

import com.yangyoung.english.attendance.domain.Attendance;
import com.yangyoung.english.attendance.domain.AttendanceRepository;
import com.yangyoung.english.attendance.domain.AttendanceType;
import com.yangyoung.english.attendance.dto.request.AttendRequest;
import com.yangyoung.english.lecture.domain.Lecture;
import com.yangyoung.english.lecture.service.LectureUtilService;
import com.yangyoung.english.student.domain.Student;
import com.yangyoung.english.student.service.StudentUtilService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentUtilService studentUtilService;
    private final LectureUtilService lectureUtilService;

    // 출석 정보 등록
    public void attend(AttendRequest request) {

        Student attendStudent = studentUtilService.findStudentById(request.getStudentId());

        // LocalDateTime -> LocalDate
        LocalDate date = request.getAttendTime().toLocalDate();

        // LocalDateTime -> LocalTime
        LocalTime time = request.getAttendTime().toLocalTime();

        LocalDateTime start = request.getAttendTime().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime end = request.getAttendTime().withHour(23).withMinute(59).withSecond(59);

        List<Lecture> lectureList = lectureUtilService.getLectureBriefByDay(date);
        for (Lecture lecture : lectureList) {

            boolean isAttended = attendanceRepository.existsByStudentAndAttendedDateTimeBetween(attendStudent, start, end);
            if (isAttended) { // 이미 출석 기록이 있는 경우
                continue;
            }

            int timeDiff = time.compareTo(lecture.getStartTime());
            if (timeDiff > 0) { // 출석 시간이 강의 시작 시간 이후
                continue;
            }

            Attendance attendance = Attendance.builder()
                    .student(attendStudent)
                    .lecture(lecture)
                    .attendanceType(AttendanceType.ATTENDANCE)
                    .attendedDateTime(request.getAttendTime())
                    .build();
            attendanceRepository.save(attendance);
        }
    }
}

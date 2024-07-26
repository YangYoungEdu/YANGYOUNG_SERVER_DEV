package com.yangyoung.english.attendance.service;

import com.yangyoung.english.attendance.domain.Attendance;
import com.yangyoung.english.attendance.domain.AttendanceRepository;
import com.yangyoung.english.attendance.domain.AttendanceType;
import com.yangyoung.english.attendance.dto.request.AttendanceUpdateRequest;
import com.yangyoung.english.attendance.dto.response.AttendanceResponse;
import com.yangyoung.english.lecture.domain.Lecture;
import com.yangyoung.english.lecture.service.LectureUtilService;
import com.yangyoung.english.student.domain.Student;
import com.yangyoung.english.student.service.StudentUtilService;
import com.yangyoung.english.studentLecture.domain.StudentLecture;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentUtilService studentUtilService;
    private final LectureUtilService lectureUtilService;

    // 출석 - 학생
    @Transactional
    public AttendanceResponse attend(Long studentId) {
        Student attendStudent = studentUtilService.findStudentById(studentId);

        LocalDateTime attendDateTime = LocalDateTime.now(); // ToDo : 프론트와의 시간 동기화 문제 해결 필요
        LocalDate date = attendDateTime.toLocalDate();
        LocalTime time = attendDateTime.toLocalTime();
        LocalDateTime startDateTime = date.atStartOfDay();
        LocalDateTime endDateTime = date.atTime(LocalTime.MAX);

        // 먼저 출석 기록이 있는지 확인
        boolean isAlreadyAttended = attendanceRepository.existsByStudentAndAttendedDateTimeBetween(attendStudent, startDateTime, endDateTime);
        if (isAlreadyAttended) {
            return new AttendanceResponse(attendStudent);
        }

        // 강의 목록 조회 및 출석 처리
        List<Lecture> lectureList = lectureUtilService.findLectureByDay(date);
        lectureList.sort(Comparator.comparing(Lecture::getStartTime)); // 시작 시간 순 정렬
        for (int i = 0; i < lectureList.size(); i++) {
            Lecture lecture = lectureList.get(i);
            Attendance attendance;
            AttendanceType attendanceType = AttendanceType.ATTENDANCE;

            if (i == 0) {
                if (time.isAfter(lecture.getEndTime())) {
                    attendanceType = AttendanceType.ABSENCE; // Todo: 지각 or 결석 처리 여부 확인 필요
                }
            }

            attendance = Attendance.builder()
                    .student(attendStudent)
                    .lecture(lecture)
                    .attendanceType(attendanceType)
                    .attendedDateTime(attendDateTime)
                    .build();
            attendanceRepository.save(attendance);
        }

        return new AttendanceResponse(attendStudent);
    }

    // 강의별 출석 조회
    @Transactional
    public List<AttendanceResponse> getAttendanceByLecture(Long lectureId, LocalDate date) {

        LocalDateTime startDateTime = date.atStartOfDay();
        LocalDateTime endDateTime = date.atTime(LocalTime.MAX);
        log.info(String.valueOf(startDateTime));
        log.info(String.valueOf(endDateTime));

        Lecture lecture = lectureUtilService.findLectureById(lectureId);
        List<Student> studentList = lecture.getStudentLectureList().stream()
                .map(StudentLecture::getStudent)
                .toList();

        // 한 번의 쿼리로 모든 출석 정보 가져오기
        Map<Long, Attendance> attendanceMap = attendanceRepository.findByLectureIdAndAttendedDateTimeBetween(lectureId, startDateTime, endDateTime)
                .stream().collect(Collectors.toMap(attendance -> attendance.getStudent().getId(), attendance -> attendance));
        log.info(String.valueOf(attendanceMap.size()));

        // 출석 정보를 포함한 응답 리스트 생성
        return studentList.stream().map(student -> {
            AttendanceResponse attendanceResponse;
            Attendance attendance = attendanceMap.get(student.getId());
            if (attendance == null) {
                attendanceResponse = new AttendanceResponse(student);
            } else {
                attendanceResponse = new AttendanceResponse(student, attendance);
            }
            return attendanceResponse;
        }).collect(Collectors.toList());
    }

    // 출석 정보 수정
    @Transactional
    public void updateAttendance(List<AttendanceUpdateRequest> requestList) {

        for (AttendanceUpdateRequest request : requestList) {
            Optional<Long> id = Optional.ofNullable(request.getId());
            Optional<Attendance> pastAttendance = Optional.empty();

            if (id.isPresent()) {
                pastAttendance = attendanceRepository.findById(id.get());
            }

            if (pastAttendance.isEmpty()) {
                if (request.getAttendDateTime() == null) {
                    continue;
                }

                log.info("New Attendance");
                Student student = studentUtilService.findStudentById(request.getStudentId());
                Lecture lecture = lectureUtilService.findLectureById(request.getLectureId());
                AttendanceType attendanceType = AttendanceType.getAttendanceType(request.getAttendanceType());

                Attendance newAttendance = Attendance.builder()
                        .student(student)
                        .lecture(lecture)
                        .attendanceType(attendanceType)
                        .attendedDateTime(request.getAttendDateTime())
                        .build();

                attendanceRepository.save(newAttendance);
            } else {
                Attendance attendance = pastAttendance.get();
                attendance.updateAttendanceType(AttendanceType.getAttendanceType(request.getAttendanceType()));
            }
        }
    }
}

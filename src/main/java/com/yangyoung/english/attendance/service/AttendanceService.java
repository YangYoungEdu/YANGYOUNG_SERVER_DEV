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
import lombok.RequiredArgsConstructor;
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
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentUtilService studentUtilService;
    private final LectureUtilService lectureUtilService;

    // 출석 - 학생
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
            Optional<Attendance> attendance = attendanceRepository.findByStudentAndAttendedDateTimeBetween(attendStudent, startDateTime, endDateTime);
            if (attendance.isPresent()) {
                attendance.get().updateAttendance(AttendanceType.ATTENDANCE, null);
            }

            return new AttendanceResponse(attendStudent);
        }

        // 강의 목록 조회 및 출석 처리
        List<Lecture> lectureList = lectureUtilService.getLectureByDay(date);
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
    public List<AttendanceResponse> getAttendanceByLecture(Long lectureId, LocalDate date) {

        LocalDateTime startDateTime = date.atStartOfDay();
        LocalDateTime endDateTime = date.atTime(LocalTime.MAX);

        Lecture lecture = lectureUtilService.findLectureById(lectureId);
        List<Student> studentList = lecture.getStudentLectureList().stream()
                .map(StudentLecture::getStudent)
                .toList();

        List<Long> studentIdList = studentList.stream()
                .map(Student::getId)
                .toList();
        // 한 번의 쿼리로 모든 출석 정보 가져오기
        Map<Long, Attendance> attendanceMap = attendanceRepository.findByStudentIdInAndAttendedDateTimeBetween(studentIdList, startDateTime, endDateTime)
                .stream().collect(Collectors.toMap(attendance -> attendance.getStudent().getId(), attendance -> attendance));

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
    public void updateAttendance(List<AttendanceUpdateRequest> requestList) {

        for (AttendanceUpdateRequest request : requestList) {
            Optional<Attendance> optionalAttendance = attendanceRepository.findById(request.getId());
            if (optionalAttendance.isEmpty()) {
                return;
            }

            Attendance attendance = optionalAttendance.get();
            attendance.updateAttendance(AttendanceType.getAttendanceType(request.getAttendanceType()), request.getNote());
        }
    }
}

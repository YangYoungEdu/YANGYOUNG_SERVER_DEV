package com.yangyoung.english.attendance.service;

import com.yangyoung.english.attendance.domain.Attendance;
import com.yangyoung.english.attendance.domain.AttendanceRepository;
import com.yangyoung.english.attendance.domain.AttendanceType;
import com.yangyoung.english.attendance.dto.request.AttendRequest;
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
    public AttendanceResponse attend(AttendRequest request) {
        Student attendStudent = studentUtilService.findStudentById(request.getStudentId());

        LocalDateTime attendDateTime = request.getAttendTime();
        LocalDate date = attendDateTime.toLocalDate();
        LocalTime time = attendDateTime.toLocalTime();
        LocalDateTime startOfDay = attendDateTime.withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = attendDateTime.withHour(23).withMinute(59).withSecond(59).withNano(999999999);

        // 먼저 출석 기록이 있는지 확인
        boolean isAlreadyAttended = attendanceRepository.existsByStudentAndAttendedDateTimeBetween(attendStudent, startOfDay, endOfDay);
        if (isAlreadyAttended) {
            Optional<Attendance> attendance = attendanceRepository.findByStudentAndAttendedDateTimeBetween(attendStudent, startOfDay, endOfDay);
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
                    attendanceType = AttendanceType.ABSENCE; // Todo: 지각 or 결석 처리
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
    public List<AttendanceResponse> getAttendanceByLecture(Long lectureId) {

        Lecture lecture = lectureUtilService.findLectureById(lectureId);
        List<Student> studentList = lecture.getStudentLectureList().stream()
                .map(StudentLecture::getStudent)
                .toList();

        List<Long> studentIdList = studentList.stream()
                .map(Student::getId)
                .toList();
        // 한 번의 쿼리로 모든 출석 정보 가져오기
        Map<Long, Attendance> attendanceMap = attendanceRepository.findByStudentIdIn(studentIdList)
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

package com.yangyoung.english.lecture.service;

import com.yangyoung.english.lecture.domain.Lecture;
import com.yangyoung.english.lecture.domain.LectureRepository;
import com.yangyoung.english.lecture.dto.request.AddLectureByFormRequest;
import com.yangyoung.english.lecture.dto.response.LectureResponse;
import com.yangyoung.english.lecture.exception.LectureNameDuplicateException;
import com.yangyoung.english.lectureDate.domain.LectureDate;
import com.yangyoung.english.lectureDate.domain.LectureDateRepository;
import com.yangyoung.english.lectureDay.domain.LectureDay;
import com.yangyoung.english.lectureDay.domain.LectureDayRepository;
import com.yangyoung.english.student.domain.Student;
import com.yangyoung.english.student.service.StudentService;
import com.yangyoung.english.student.service.StudentUtilService;
import com.yangyoung.english.studentLecture.domain.StudentLecture;
import com.yangyoung.english.studentLecture.domain.StudentLectureRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LectureService {

    private final static String LECTURE_NAME_DUPLICATED_MESSAGE = "Lecture name is already exist. (lectureName: %s)";
    private final LectureRepository lectureRepository;
    private final LectureDateRepository lectureDateRepository;
    private final LectureDayRepository lectureDayRepository;
    private final StudentLectureRepository studentLectureRepository;
    private final StudentUtilService studentUtilService;
    private final Logger logger = LoggerFactory.getLogger(StudentService.class);

    // 강의 정보 등록 - 폼 입력으로 등록
    @Transactional
    public LectureResponse addLectureByForm(AddLectureByFormRequest request) {

        isNameDuplicated(request.getName());  // 강의명 중복 검사

        Lecture newLecture = request.toEntity();
        lectureRepository.save(newLecture); // 강의 저장

        assignLectureDateAndDay(newLecture, request.getLectureDayList(), request.getLectureDateList()); // 강의 -> 날짜/요일 할당

        assignLectureStudents(newLecture, request.getStudentList()); // 강의 -> 학생 할당

        return new LectureResponse(newLecture);
    }

    // 강의명 중복 검사
    private void isNameDuplicated(String name) {
        boolean isDuplicated = lectureRepository.existsByName(name);
        if (isDuplicated) {
            String errorMessage = String.format(LECTURE_NAME_DUPLICATED_MESSAGE, name);
            logger.error(errorMessage);
            throw new LectureNameDuplicateException(errorMessage);
        }
    }

    // 강의 -> 날짜/요일 할당
    private void assignLectureDateAndDay(Lecture lecture, List<DayOfWeek> dayList, List<LocalDate> dateList) {

        // 강의 -> 날짜 할당
        for (LocalDate date : dateList) {
            lectureDateRepository.save(new LectureDate(date, lecture));
        }

        // 강의 -> 요일 할당
        for (DayOfWeek day : dayList) {
            lectureDayRepository.save(new LectureDay(day, lecture));
        }
    }

    // 강의 -> 학생 할당
    private void assignLectureStudents(Lecture lecture, List<Long> studentList) {

        List<StudentLecture> studentLectureList = new ArrayList<>();

        for (Long studentId : studentList) {
            Student student = studentUtilService.findStudentById(studentId);
            studentLectureList.add(new StudentLecture(student, lecture));
        }
        studentLectureRepository.saveAll(studentLectureList);
    }

    // 강의 전체 조회 - 페이징 처리
    @Transactional
    public Page<LectureResponse> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return lectureRepository.findAll(pageable).map(LectureResponse::new);
    }

    // 강의 상세 조회
    @Transactional
    public LectureResponse findById(Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow();
        return new LectureResponse(lecture);
    }

    // 강의 수강 학생 조회
    @Transactional
    public List<Student> findStudentsByLectureId(Long lectureId) {

        return studentLectureRepository.findByLectureId(lectureId);
    }
}

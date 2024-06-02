package com.yangyoung.english.lecture.service;

import com.yangyoung.english.lecture.domain.Lecture;
import com.yangyoung.english.lecture.domain.LectureRepository;
import com.yangyoung.english.lecture.dto.request.AddLectureByFormRequest;
import com.yangyoung.english.lecture.dto.request.LectureStudentAddRequest;
import com.yangyoung.english.lecture.dto.request.LectureStudentUpdateRequest;
import com.yangyoung.english.lecture.dto.request.LectureUpdateRequest;
import com.yangyoung.english.lecture.dto.response.LectureBriefResponse;
import com.yangyoung.english.lecture.dto.response.LectureResponse;
import com.yangyoung.english.lecture.exception.LectureErrorCode;
import com.yangyoung.english.lecture.exception.LectureNameDuplicateException;
import com.yangyoung.english.lecture.exception.LectureNotFoundException;
import com.yangyoung.english.lectureDate.domain.LectureDate;
import com.yangyoung.english.lectureDate.domain.LectureDateRepository;
import com.yangyoung.english.lectureDay.domain.LectureDay;
import com.yangyoung.english.lectureDay.domain.LectureDayRepository;
import com.yangyoung.english.student.domain.Student;
import com.yangyoung.english.student.service.StudentUtilService;
import com.yangyoung.english.studentLecture.domain.StudentLecture;
import com.yangyoung.english.studentLecture.domain.StudentLectureRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class LectureService {

    private final LectureRepository lectureRepository;
    private final LectureDateRepository lectureDateRepository;
    private final LectureDayRepository lectureDayRepository;
    private final StudentLectureRepository studentLectureRepository;
    private final StudentUtilService studentUtilService;
    private final LectureUtilService lectureUtilService;

    // 강의 종료 여부 확인
    // second minute hour day-of-month month day-of-week
    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    @Transactional
    public void checkLectureStatus() {
        LocalDate today = LocalDate.now();

        List<Lecture> lectureList = lectureRepository.findByIsFinishedFalse();
        for (Lecture lecture : lectureList) {
            LocalDate lastDate = lecture.getLectureDateList().stream()
                    .map(LectureDate::getLectureDate)
                    .max(LocalDate::compareTo)
                    .orElse(null);

            if (lastDate != null && lastDate.isAfter(today)) {
                lecture.updateIsFinished();
            }
        }
    }

    // 강의 정보 등록 - 폼 입력으로 등록
    @Transactional
    public LectureResponse addLectureByForm(AddLectureByFormRequest request) {

        isNameDuplicated(request.getName());  // 강의명 중복 검사

        Lecture newLecture = request.toEntity();
        lectureRepository.save(newLecture); // 강의 저장

        assignLectureDate(newLecture, request.getLectureDateList()); // 강의 -> 날짜/요일 할당
        assignLectureDay(newLecture, request.getLectureDayList()); // 강의 -> 요일 할당

        assignLectureStudents(newLecture, request.getStudentList()); // 강의 -> 학생 할당

        return new LectureResponse(newLecture);
    }

    // 강의 학생 추가
    @Transactional
    public void addStudentToLecture(LectureStudentAddRequest request) {

        Lecture lecture = lectureUtilService.findLectureById(request.getLectureId());
        assignLectureStudents(lecture, request.getStudentIdList());
    }

    // 강의명 중복 검사
    private void isNameDuplicated(String name) {
        boolean isDuplicated = lectureRepository.existsByName(name);
        if (isDuplicated) {
            LectureErrorCode lectureErrorCode = LectureErrorCode.LECTURE_NAME_DUPLICATED;
            throw new LectureNameDuplicateException(lectureErrorCode, name);
        }
    }

    // 강의 -> 날짜 할당
    private void assignLectureDate(Lecture lecture, List<LocalDate> dateList) {

        for (LocalDate date : dateList) { // 날짜 할당
            lectureDateRepository.save(new LectureDate(date, lecture));
        }
    }

    // 강의 -> 날짜 할당
    private void assignLectureDay(Lecture lecture, List<DayOfWeek> dayList) {

        for (DayOfWeek day : dayList) { // 요일 할당
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
    public Page<LectureResponse> getAllLecture(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return lectureRepository.findAll(pageable).map(LectureResponse::new);
    }

    // 강의 전체 조회 - 달 단위
    @Transactional
    public List<LectureResponse> getAllLectureByMonth(int year, int month) {

        return lectureRepository.findLecturesByYearAndMonth(year, month).stream()
                .map(LectureResponse::new)
                .toList();
    }

    // 강의 전체 조회 - 주 단위
    @Transactional
    public List<LectureResponse> getAllLectureByWeek(LocalDate date) {

        WeekFields weekFields = WeekFields.of(Locale.KOREA);

        LocalDate firstDayOfWeek = date.with(weekFields.dayOfWeek(), 1);
        LocalDate lastDayOfWeek = date.with(weekFields.dayOfWeek(), 7);

        return lectureRepository.findByLectureDateList_LectureDateBetween(firstDayOfWeek, lastDayOfWeek).stream()
                .map(LectureResponse::new)
                .toList();
    }

    // 강의 전체 조회 - 일 단위 & 시간 오름차순 정렬
    @Transactional
    public List<LectureResponse> getAllLectureByDate(LocalDate date) {

        return lectureRepository.findLecturesByDate(date).stream()
                .sorted((l1, l2) -> l1.getStartTime().compareTo(l2.getStartTime()))
                .map(LectureResponse::new)
                .toList();
    }

    // 강의 상세 조회
    @Transactional
    public LectureResponse getLecture(Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow();
        return new LectureResponse(lecture);
    }

    // 강의 수강 학생 조회
    @Transactional
    public List<Student> findStudentsByLectureId(Long lectureId) {

        return studentLectureRepository.findStudentsByLectureId(lectureId);
    }

    // 강의 정보 수정
    @Transactional
    public LectureResponse updateLecture(LectureUpdateRequest request) {

        Lecture lecture = lectureUtilService.findLectureById(request.getId());

        lecture.update(request.getName(), request.getTeacher(), request.getRoom(), request.getStartTime(), request.getEndTime(), request.isDailyRepeat(), request.isWeeklyRepeat(), request.isMonthlyRepeat(), request.isYearlyRepeat());

        lectureDateRepository.deleteByLectureId(lecture.getId());
        lectureDayRepository.deleteByLectureId(lecture.getId());
        assignLectureDate(lecture, request.getLectureDateList());

        return new LectureResponse(lecture);
    }

    // 강의 수강 학생 수정
    @Transactional
    public LectureResponse updateLectureStudents(LectureStudentUpdateRequest request) {

        Lecture lecture = lectureUtilService.findLectureById(request.getLectureId());

        studentLectureRepository.deleteByLectureId(lecture.getId());
        assignLectureStudents(lecture, request.getStudentIdList());

        return new LectureResponse(lecture);
    }

    // 강의 삭제 - single
    @Transactional
    public void deleteLecture(Long lectureId) {

        boolean isExist = lectureRepository.existsById(lectureId);
        if (!isExist) {
            LectureErrorCode lectureErrorCode = LectureErrorCode.LECTURE_NOT_FOUND;
            throw new LectureNotFoundException(lectureErrorCode, lectureId);
        }

        lectureRepository.deleteById(lectureId);
    }

    // 강의 삭제 - multiple
    @Transactional
    public void deleteLectures(List<Long> lectureIdList) {
        for (Long lectureId : lectureIdList) {
            deleteLecture(lectureId);
        }
    }

    // 특정 학생이 수강하는 강의 조회
    @Transactional
    public List<LectureBriefResponse> getLecturesByStudent(Long studentId) {

        List<Lecture> studentList = studentLectureRepository.findLecturesByStudentId(studentId);

        return studentList.stream().
                map(LectureBriefResponse::new)
                .toList();
    }
}

package com.yangyoung.english.lecture.service;

import com.yangyoung.english.lecture.domain.Lecture;
import com.yangyoung.english.lecture.domain.LectureRepository;
import com.yangyoung.english.lecture.domain.LectureType;
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
import com.yangyoung.english.section.domain.Section;
import com.yangyoung.english.section.domain.SectionRepository;
import com.yangyoung.english.section.service.SectionUtilService;
import com.yangyoung.english.student.domain.Student;
import com.yangyoung.english.student.service.StudentUtilService;
import com.yangyoung.english.studentLecture.domain.StudentLecture;
import com.yangyoung.english.studentLecture.domain.StudentLectureRepository;
import com.yangyoung.english.util.spreasheet.SheetsService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.WeekFields;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class LectureService {

    private static final int REQUIRED_FIELDS = 7;
    private static final int LECTURE_NAME_INDEX = 1;

    private final LectureRepository lectureRepository;
    private final LectureDateRepository lectureDateRepository;
    private final LectureDayRepository lectureDayRepository;
    private final StudentLectureRepository studentLectureRepository;
    private final StudentUtilService studentUtilService;
    private final LectureUtilService lectureUtilService;
    private final SectionRepository sectionRepository;
    private final SectionUtilService sectionUtilService;

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

        this.assignLectureStudentsWithId(newLecture, request.getStudentList()); // 강의 -> 학생 할당

        return new LectureResponse(newLecture);
    }

    // 강의 학생 추가 - 폼
    @Transactional
    public void addStudentToLecture(LectureStudentAddRequest request) {

        Lecture lecture = lectureUtilService.findLectureById(request.getLectureId());
        this.assignLectureStudentsWithId(lecture, request.getStudentIdList());
    }

    // 강의명 중복 검사 - 폼
    private void isNameDuplicated(String name) {
        boolean isDuplicated = lectureRepository.existsByName(name);
        if (isDuplicated) {
            LectureErrorCode lectureErrorCode = LectureErrorCode.LECTURE_NAME_DUPLICATED;
            throw new LectureNameDuplicateException(lectureErrorCode, name);
        }
    }

    // 강의 -> 학생 할당 - 폼
    private void assignLectureStudentsWithId(Lecture lecture, List<Long> studentIdList) {

        List<StudentLecture> studentLectureList = new ArrayList<>();

        for (Long studentId : studentIdList) {
            Student student = studentUtilService.findStudentById(studentId);
            studentLectureList.add(new StudentLecture(student, lecture));
        }
        studentLectureRepository.saveAll(studentLectureList);
    }

    @Scheduled(cron = "0 0 0 * * FRI") // 매주 금요일 자정에 실행
    @Transactional
    public void addLectureBySheet() throws GeneralSecurityException, IOException {
        List<List<Object>> lectureDataList = SheetsService.readSpreadSheet("강의");
        List<Lecture> lectureList = new ArrayList<>();

        for (List<Object> lectureData : lectureDataList) {
            log.info("size: {}", lectureData.size());

            if (!isLectureDataValid(lectureData)) {
                continue;
            }

            Lecture newLecture = createLectureFromData(lectureData);
            lectureRepository.save(newLecture);

            String preset = lectureData.get(7).toString();
            if (!preset.isBlank()) { // 프리셋이 존재할 경우
                assignLectureStudents(newLecture, preset);
            }

            String school = lectureData.get(8).toString();
            if (!school.isBlank()) { // 학교가 존재할 경우
                String studentName = lectureData.get(9).toString();
                if (!studentName.isBlank()) {
                    Student student = studentUtilService.findStudentByNameAndSchool(studentName, school);
                    if (student != null) {
                        assignLectureStudents(newLecture, student);
                    }
                }
            }
        }

//        lectureRepository.saveAll(lectureList);

        log.info("Newly added lectures: {}", lectureList.size());
    }

    private List<Lecture> extractLectures(List<Map<Lecture, List<Student>>> lectureStudentList) {
        List<Lecture> lectureList = new ArrayList<>();
        for (Map<Lecture, List<Student>> map : lectureStudentList) {
            lectureList.addAll(map.keySet());
        }
        return lectureList;
    }

    private void assignLectureStudents(Lecture lecture, String preset) {
        List<String> presetList = Arrays.asList(preset.split(","));
        List<Section> sections = sectionRepository.findByNameIn(presetList);

        if (!sections.isEmpty()) {
            List<StudentLecture> studentLectureList = new ArrayList<>();
            for (Section section : sections) {
                for (Student student : section.getStudentList()) {
                    studentLectureList.add(new StudentLecture(student, lecture));
                }
            }
            studentLectureRepository.saveAll(studentLectureList);
        }
    }

    private void assignLectureStudents(Lecture lecture, Student student) {
        studentLectureRepository.save(new StudentLecture(student, lecture));
    }

    // 강의 필수 정보 확인(강의 유형, 강의명, 강사, 강의실, 날짜, 시작 시간, 종료 시간)
    private boolean isLectureDataValid(List<Object> lectureData) {
        if (lectureData == null || lectureData.size() < REQUIRED_FIELDS) {
            return false;
        }

        for (int i = 0; i < REQUIRED_FIELDS; i++) {
            if (lectureData.get(i) == null || lectureData.get(i).toString().isBlank()) {
                return false;
            }
        }

        String lectureName = lectureData.get(LECTURE_NAME_INDEX).toString();
        if (lectureRepository.existsByName(lectureName)) {
            return false;
        }

        return true;
    }


    // 강의 정보 생성
    private Lecture createLectureFromData(List<Object> lectureData) {
        LectureType lectureType = LectureType.getLectureTypeName(lectureData.get(0).toString());
        String name = lectureData.get(1).toString();
        String teacher = lectureData.get(2).toString();
        String room = lectureData.get(3).toString();
        LocalTime startTime = LocalTime.parse(lectureData.get(5).toString());
        LocalTime endTime = LocalTime.parse(lectureData.get(6).toString());

        return Lecture.builder()
                .lectureType(lectureType)
                .name(name)
                .teacher(teacher)
                .room(room)
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }

    // 강의 학생 할당 - 스프레드시트 - 프리셋
    @Transactional
    public void assignLectureStudentsWithId(Lecture lecture, String preset) {
        List<String> presetList = Arrays.asList(preset.split(","));
        List<Section> sectionList = sectionRepository.findByNameIn(presetList);

        if (sectionList.isEmpty()) {
            return;
        }

        List<StudentLecture> studentLectureList = new ArrayList<>();
        for (Section section : sectionList) {
            for (Student student : section.getStudentList()) {
                studentLectureList.add(new StudentLecture(student, lecture));
            }
        }

        studentLectureRepository.saveAll(studentLectureList);
    }

    // 강의 학생 할당 - 스프레드시트 - 학교
    @Transactional
    public void assignLectureStudentsWithStudent(Lecture lecture, List<Student> studentList) {
        List<StudentLecture> studentLectureList = new ArrayList<>();
        for (Student student : studentList) {
            studentLectureList.add(new StudentLecture(student, lecture));
        }
        studentLectureRepository.saveAll(studentLectureList);
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

        lecture.update(request.getName(), request.getTeacher(), request.getRoom(), request.getStartTime(), request.getEndTime());

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
        this.assignLectureStudentsWithId(lecture, request.getStudentIdList());

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

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
import com.yangyoung.english.lectureDate.domain.LectureDate;
import com.yangyoung.english.lectureDate.domain.LectureDateRepository;
import com.yangyoung.english.lectureDay.domain.LectureDay;
import com.yangyoung.english.lectureDay.domain.LectureDayRepository;
import com.yangyoung.english.lectureSection.domain.LectureSection;
import com.yangyoung.english.lectureSection.domain.LectureSectionRepository;
import com.yangyoung.english.section.domain.Section;
import com.yangyoung.english.section.domain.SectionRepository;
import com.yangyoung.english.section.service.SectionUtilService;
import com.yangyoung.english.student.domain.Student;
import com.yangyoung.english.student.service.StudentUtilService;
import com.yangyoung.english.studentLecture.domain.StudentLecture;
import com.yangyoung.english.studentLecture.domain.StudentLectureRepository;
import com.yangyoung.english.studentSection.domain.StudentSectionRepository;
import com.yangyoung.english.util.UtilService;
import com.yangyoung.english.util.spreasheet.SheetsService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LectureService {

    private static final int REQUIRED_FIELDS = 7;
    private static final int LECTURE_LECTURE_CODE_INDEX = 0;
    private static final int LECTURE_NAME_INDEX = 1;
    private static final int LECTURE_TEACHER_INDEX = 2;
    private static final int LECTURE_ROOM_INDEX = 3;
    private static final int LECTURE_DAY_INDEX = 4;
    private static final int LECTURE_DATE_INDEX = 5;
    private static final int LECTURE_START_TIME_INDEX = 6;
    private static final int LECTURE_END_TIME_INDEX = 7;
    private static final int LECTURE_PRESET_INDEX = 8;
    private static final int LECTURE_SCHOOL_INDEX = 9;
    private static final int LECTURE_STUDENT_INDEX = 10;

    private final LectureRepository lectureRepository;
    private final LectureDateRepository lectureDateRepository;
    private final LectureDayRepository lectureDayRepository;
    private final StudentLectureRepository studentLectureRepository;
    private final StudentUtilService studentUtilService;
    private final LectureUtilService lectureUtilService;
    private final SectionRepository sectionRepository;
    private final SectionUtilService sectionUtilService;
    private final StudentSectionRepository studentSectionRepository;
    private final LectureSectionRepository lectureSectionRepository;

    // 강의 종료 여부 확인
    // second minute hour day-of-month month day-of-week
    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    @Transactional
    public void checkLectureStatus() {

        LocalDate today = LocalDate.now();

        List<Lecture> lectureList = lectureRepository.findByIsFinishedFalse();
        for (Lecture lecture : lectureList) {
            LocalDate lastDate = lecture.getLectureDateList().get(lecture.getLectureDateList().size() - 1).getLectureDate();

            if (lastDate != null && lastDate.isAfter(today)) { // 마지막 강의 날짜가 오늘 이전일 경우 강의 종료 처리
                lecture.updateIsFinished();
            }
        }
    }

    // 강의 정보 등록 - 폼 입력으로 등록
    // ToDo: 2024-07-10: 강의 추가 시 요일 선택 필요?
    @Transactional
    public LectureResponse addLectureByForm(AddLectureByFormRequest request) {

        String lectureCode = createLectureCode();

        boolean isRepeated = false;
        if (request.getLectureDateList().size() > 1) {
            isRepeated = true;
        }
        Lecture newLecture = request.toEntity(isRepeated);
        lectureRepository.save(newLecture);

        assignLectureDate(newLecture, request.getLectureDateList()); // 강의 -> 날짜/요일 할당

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
    private String createLectureCode() {
        while (true) {
            String lectureCode = UtilService.generateLectureCode();
            if (!lectureRepository.existsByLectureCode(lectureCode)) {
                return lectureCode;
            }
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

    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    @Transactional
    public void addLectureBySheet() throws GeneralSecurityException, IOException, URISyntaxException {

        List<List<Object>> lectureDataList = SheetsService.readSpreadSheet("강의");

        Lecture tempLecture = null;
        String tempSchool = null;
        for (List<Object> lectureData : lectureDataList) {

            Lecture newLecture = null;

            if (!isLectureDataValid(lectureData) && tempLecture == null) { // 강의 필수 정보가 없고 이전 강의가 없을 경우
                continue;
            }

            Optional<Lecture> isLectureExist = lectureRepository.findByLectureCode(lectureData.get(LECTURE_NAME_INDEX).toString());
            if (isLectureExist.isPresent()) { // 강의가 이미 존재할 경우
                log.info("Lecture already exists: {}", lectureData.get(LECTURE_LECTURE_CODE_INDEX).toString());
                continue;
            }

            if (isLectureDataValid(lectureData)) { // 강의 필수 정보가 존재할 경우
                // ToDo: 2024-07-10: 강의가 존재 할 때 업데이트 될 수 있도록 수정


                newLecture = createLectureFromData(lectureData);
                tempLecture = newLecture;
                lectureRepository.save(newLecture);

                List<DayOfWeek> dayList = Arrays.stream(lectureData.get(LECTURE_DAY_INDEX).toString().split(","))
                        .map(LectureDay::convertLectureDay)
                        .toList();
                List<LocalDate> dateList = Arrays.stream(lectureData.get(LECTURE_DATE_INDEX).toString().split(","))
                        .map(LocalDate::parse)
                        .toList();
                assignLectureDayAndDate(newLecture, dayList, dateList);

                String preset = lectureData.get(LECTURE_PRESET_INDEX).toString();
                if (!preset.isBlank()) { // 프리셋이 존재할 경우
                    assignLectureStudents(tempLecture, preset);

                    Section section = sectionUtilService.findSectionByName(preset);
                    assignLecturesToSection(tempLecture, section);
                }

                String school = lectureData.get(LECTURE_SCHOOL_INDEX).toString();
                if (!school.isBlank() || tempSchool != null) { // 학교가 존재할 경우
                    tempSchool = school;
                    String studentName = lectureData.get(LECTURE_STUDENT_INDEX).toString();
                    if (!studentName.isBlank()) {
                        Optional<Student> student = studentUtilService.findStudentByNameAndSchoolOptional(studentName, tempSchool);
                        if (student.isPresent()) {
                            assignLectureStudents(tempLecture, student.get());
                        }
                    }
                }
            }
        }
    }

    // 강의 -> 학생 할당 - 스프레드시트
    private void assignLectureStudents(Lecture lecture, String preset) {
        log.info("lecture: {}", lecture.getName());
        String[] presetList = preset.split(",");
        List<Section> sections = new ArrayList<>();
        for (String s : presetList) {
            Optional<Section> section = sectionRepository.findByName(s);
            section.ifPresent(sections::add);
        }

        if (!sections.isEmpty()) {
            List<StudentLecture> studentLectureList = new ArrayList<>();
            for (Section section : sections) {
                List<Student> studentList = studentSectionRepository.findStudentsBySectionId(section.getId());
                for (Student student : studentList) {
                    studentLectureList.add(new StudentLecture(student, lecture));
                }
            }
            studentLectureRepository.saveAll(studentLectureList);
        }
    }

    // 강의 -> 학생 할당 - 스프레드시트
    private void assignLectureStudents(Lecture lecture, Student student) {
        studentLectureRepository.save(new StudentLecture(student, lecture));
    }

    // 강의 -> 분반 할당
    private void assignLecturesToSection(Lecture lecture, Section section) {
        lectureSectionRepository.save(new LectureSection(lecture, section));
    }

    // 강의 필수 정보 확인(과목 코드, 과목 이름)
    private boolean isLectureDataValid(List<Object> lectureData) {

        String lectureName = lectureData.get(LECTURE_NAME_INDEX).toString();
        String teacher = lectureData.get(LECTURE_TEACHER_INDEX).toString();
        List<String> dayList = Arrays.asList(lectureData.get(LECTURE_DAY_INDEX).toString().split(","));
        List<String> dateList = Arrays.asList(lectureData.get(LECTURE_DATE_INDEX).toString().split(","));
        String startTime = lectureData.get(LECTURE_START_TIME_INDEX).toString();
        String endTime = lectureData.get(LECTURE_END_TIME_INDEX).toString();

        return !lectureName.isBlank() &&
                !teacher.isBlank() &&
                !dayList.isEmpty() &&
                !dateList.isEmpty() &&
                !startTime.isBlank() &&
                !endTime.isBlank();
    }


    // 강의 정보 생성
    private Lecture createLectureFromData(List<Object> lectureData) {
        if (lectureData == null || lectureData.size() < REQUIRED_FIELDS) {
            throw new IllegalArgumentException("Invalid lecture data");
        }

        String lectureCode = lectureData.get(LECTURE_LECTURE_CODE_INDEX).toString();
        String name = lectureData.get(LECTURE_NAME_INDEX).toString();
        String teacher = lectureData.get(LECTURE_TEACHER_INDEX).toString();
        String room = lectureData.get(LECTURE_ROOM_INDEX).toString();
        LocalTime startTime = LocalTime.parse(lectureData.get(LECTURE_START_TIME_INDEX).toString());
        LocalTime endTime = LocalTime.parse(lectureData.get(LECTURE_END_TIME_INDEX).toString());

        return Lecture.builder()
                .lectureCode(lectureCode)
                .name(name)
                .teacher(teacher)
                .room(room)
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }

    private void assignLectureDayAndDate(Lecture lecture, List<DayOfWeek> dayList, List<LocalDate> dateList) {

        List<LocalDate> dateListByDay = new ArrayList<>();

        if (!dayList.isEmpty()) {
            LocalDate date = dateList.get(0);
            int year = date.getYear();
            int month = date.getMonthValue();
            for (DayOfWeek dayOfWeek : dayList) {
                while (date.getMonthValue() == month) {
                    if (date.getDayOfWeek() == dayOfWeek) {
                        dateListByDay.add(date); // 주어진 요일과 일치하는 날짜를 리스트에 추가
                    }
                    date = date.plusDays(1); // 다음 날짜로 이동
                }
            }
        } else {
            dateListByDay = dateList;
        }

        assignLectureDay(lecture, dayList);
        assignLectureDate(lecture, dateListByDay);
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

    // 강의 전체 조회 - 달 단위
    @Transactional
    public List<LectureResponse> getAllLectureByMonth(int year, int month) {

        List<LectureDate> lectureDateList = lectureDateRepository.findByYearAndMonth(year, month);

        return lectureDateList.stream()
                .map(LectureResponse::new)
                .collect(Collectors.toList());
    }

    // 강의 전체 조회 - 주 단위
    @Transactional
    public List<LectureResponse> getAllLectureByWeek(LocalDate date) {

        WeekFields weekFields = WeekFields.of(Locale.KOREA);

        LocalDate firstDayOfWeek = date.with(weekFields.dayOfWeek(), 1);
        LocalDate lastDayOfWeek = date.with(weekFields.dayOfWeek(), 7);

        List<LectureDate> lectureDateList = lectureDateRepository.findByDateRange(firstDayOfWeek, lastDayOfWeek);
        return lectureDateList.stream()
                .map(LectureResponse::new)
                .collect(Collectors.toList());
    }

    // 강의 전체 조회 - 일 단위
    @Transactional
    public List<LectureResponse> getAllLectureByDate(LocalDate date) {

        List<LectureDate> lectureDateList = lectureDateRepository.findByDate(date);

        return lectureDateList.stream()
                .map(LectureResponse::new)
                .collect(Collectors.toList());
    }

    // 강의 상세 조회
    @Transactional
    public LectureResponse getLecture(Long lectureId) {

        Optional<LectureDate> lectureDate = lectureDateRepository.findById(lectureId);
        if (lectureDate.isEmpty()) {
            return null;
        }

        return new LectureResponse(lectureDate.get());
    }

    // 강의 정보 수정
    @Transactional
    public LectureResponse updateLecture(LectureUpdateRequest request) {

        Optional<LectureDate> lectureDate = lectureDateRepository.findById(request.getId());
        if (lectureDate.isEmpty()) {
            return null;
        }
        Lecture lecture = lectureDate.get().getLecture();

        if (request.isAllUpdate()) {
            lecture.update(request.getName(), request.getTeacher(), request.getRoom(), request.getStartTime(), request.getEndTime());
        }
        if (!request.isAllUpdate()) {
            lectureDateRepository.deleteByLectureId(lecture.getId());

            Lecture newLecture = request.toEntity();
            lectureRepository.save(newLecture);
            lectureDateRepository.save(new LectureDate(lectureDate.get().getLectureDate(), newLecture));
        }

        return new LectureResponse(lectureDate.get());
    }

    // 강의 수강 학생 수정
    @Transactional
    public LectureResponse updateLectureStudents(LectureStudentUpdateRequest request) {

        Optional<LectureDate> lectureDate = lectureDateRepository.findById(request.getLectureId());
        if (lectureDate.isEmpty()) {
            return null;
        }

        Lecture lecture = lectureDate.get().getLecture();

        studentLectureRepository.deleteByLectureId(lecture.getId());
        this.assignLectureStudentsWithId(lecture, request.getStudentIdList());

        return new LectureResponse(lectureDate.get());
    }

    // 강의 삭제
    @Transactional
    public void deleteLectures(List<Long> lectureIdList) {

        List<Long> deletedLectureIdList = new ArrayList<>();

        for (Long lectureId : lectureIdList) {

            boolean isExist = lectureRepository.existsById(lectureId);
            if (!isExist) {
                LectureErrorCode lectureErrorCode = LectureErrorCode.LECTURE_NOT_FOUND;
                log.warn("Lecture not found: {}", lectureId);

                return;
            }

            deletedLectureIdList.add(lectureId);
        }

        lectureRepository.deleteAllById(deletedLectureIdList);
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

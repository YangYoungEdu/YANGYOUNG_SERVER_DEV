package com.yangyoung.english.student.service;

import com.yangyoung.english.configuration.OneIndexedPageable;
import com.yangyoung.english.lecture.domain.Lecture;
import com.yangyoung.english.lecture.dto.response.LectureBriefResponse;
import com.yangyoung.english.lecture.service.LectureUtilService;
import com.yangyoung.english.school.domain.School;
import com.yangyoung.english.school.domain.SchoolRepository;
import com.yangyoung.english.school.domain.Status;
import com.yangyoung.english.school.service.SchoolUtilService;
import com.yangyoung.english.section.domain.Section;
import com.yangyoung.english.section.service.SectionUtilService;
import com.yangyoung.english.student.domain.Grade;
import com.yangyoung.english.student.domain.Student;
import com.yangyoung.english.student.domain.StudentRepository;
import com.yangyoung.english.student.dto.request.StudentAddRequest;
import com.yangyoung.english.student.dto.request.StudentsDischargeRequest;
import com.yangyoung.english.student.dto.response.StudentBriefResponse;
import com.yangyoung.english.student.dto.response.StudentResponse;
import com.yangyoung.english.student.dto.response.StudentScheduleResponse;
import com.yangyoung.english.student.exception.StudentErrorCode;
import com.yangyoung.english.student.exception.StudentIdDuplicateException;
import com.yangyoung.english.studentLecture.domain.StudentLecture;
import com.yangyoung.english.task.domain.Task;
import com.yangyoung.english.task.dto.response.TaskBriefResponse;
import com.yangyoung.english.task.service.TaskUtilService;
import com.yangyoung.english.util.UtilService;
import com.yangyoung.english.util.spreasheet.SheetsService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentService {

    private final static int REQUIRED_DATA = 7;
    private final static int STUDENT_ID_INDEX = 0;
    private final static int STUDENT_NAME_INDEX = 1;
    private final static int STUDENT_GRADE_INDEX = 2;
    private final static int STUDENT_SCHOOL_INDEX = 3;
    private final static int STUDENT_STUDENT_PHONE_NUMBER_INDEX = 4;
    private final static int STUDENT_PARENT_PHONE_NUMBER_INDEX = 5;
    private final static int STUDENT_SECTION_INDEX = 6;
    private final StudentRepository studentRepository;
    private final SchoolUtilService schoolUtilService;
    private final StudentUtilService studentUtilService;
    private final LectureUtilService lectureUtilService;
    private final TaskUtilService taskUtilService;
    private final SchoolRepository schoolRepository;
    private final SectionUtilService sectionUtilService;

    // 학생 정보 등록 - 폼 입력으로 등록
    @Transactional
    public StudentResponse addStudentByForm(StudentAddRequest request) {

//        boolean isDataValid = validateStudentData(request);
//        if (!isDataValid) { // 필수 데이터 확인
//            log.error("학생 데이터가 충분하지 않습니다.");
//        }

        boolean isIdDuplicate = studentRepository.existsById(request.getId());
        if (isIdDuplicate) { // id 중복 검사
            StudentErrorCode studentErrorCode = StudentErrorCode.STUDENT_ID_DUPLICATED;
            throw new StudentIdDuplicateException(studentErrorCode, request.getId());
        }

        School school = schoolUtilService.findSchoolByName(request.getSchool());
        Section section = sectionUtilService.findSectionByName(request.getSection());
        Student newStudent = request.toEntity(school, section);
        studentRepository.save(newStudent);

        return new StudentResponse(newStudent);
    }

    // 학생 정보 등록 - 스프레드시트로 등록
    // ToDo : 실행시간 설정 변경
    @Scheduled(cron = "0 0 0 * * FRI")
    @Transactional
    public void addStudentsBySheet() throws Exception {

        List<Student> newStudentList = new ArrayList<>();

        List<List<Object>> studentListData = SheetsService.readSpreadSheet("학생");
        for (List<Object> studentData : studentListData) {
            StudentAddRequest request = StudentAddRequest.of(studentData);
            if (validateStudentData(studentData)) { // 필수 데이터 확인
                continue;
            }


            Student existingStudent = studentUtilService.findStudentById(request.getId());
            if (existingStudent != null) {
                if (isNeedToUpdate(existingStudent, studentData)) {
                    existingStudent.update(studentData);
                }
            }

            School school = schoolUtilService.findSchoolByName(studentData.get(STUDENT_SCHOOL_INDEX).toString());
            Section section = sectionUtilService.findSectionByName(studentData.get(STUDENT_SECTION_INDEX).toString());
            Student newStudent = new Student(studentData, school, section);
            newStudentList.add(newStudent);
        }

        if (!newStudentList.isEmpty()) {
            studentRepository.saveAll(newStudentList);
        }
    }

    // 필수항목 확인
    // ToDo : 필수 데이터 기준 수정 필요
    private boolean validateStudentData(List<Object> studentData) {

        if (studentData == null || studentData.size() < REQUIRED_DATA) {
            return false;
        }

        if (!isNumeric(studentData.get(STUDENT_ID_INDEX))) {
            log.error("학생 아이디가 숫자가 아닙니다.");
            return false;
        }

        boolean isDataEmpty = isNullOrBlank(studentData.get(STUDENT_NAME_INDEX)) ||
                isNullOrBlank(studentData.get(STUDENT_GRADE_INDEX)) ||
                isNullOrBlank(studentData.get(STUDENT_SCHOOL_INDEX)) ||
                isNullOrBlank(studentData.get(STUDENT_STUDENT_PHONE_NUMBER_INDEX));

        if (isDataEmpty) {
            log.error("학생 데이터 중 필수 데이터가 비어있습니다.");
        }

        return !isDataEmpty;
    }

    private boolean isNullOrBlank(Object obj) {
        return obj == null || obj.toString().isBlank();
    }

    private boolean isNumeric(Object obj) {
        if (obj == null) return false;
        try {
            Long.parseLong(obj.toString());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // 업데이트 필요 여부 확인
    private boolean isNeedToUpdate(Student existingStudent, List<Object> studentData) {
        return !existingStudent.getName().equals(studentData.get(STUDENT_NAME_INDEX).toString()) ||
                !existingStudent.getSchool().getName().equals(studentData.get(STUDENT_SCHOOL_INDEX).toString()) ||
                !existingStudent.getGrade().getGradeName().equals(studentData.get(STUDENT_GRADE_INDEX).toString()) ||
                !existingStudent.getStudentPhoneNumber().equals(studentData.get(STUDENT_STUDENT_PHONE_NUMBER_INDEX).toString()) ||
                !existingStudent.getParentPhoneNumber().equals(studentData.get(STUDENT_PARENT_PHONE_NUMBER_INDEX).toString());
    }


    // 학생 전체 조회 - 페이징 처리
    @Transactional
    public Page<StudentResponse> getAllStudents(int page, int size) {

        OneIndexedPageable oneIndexedPageable = UtilService.setOneIndexedPageable(page, size);

        return studentRepository.findByIsEnrolled(oneIndexedPageable, true)
                .map(StudentResponse::new);
    }

    // 숨김 학생 전체 조회 - 페이징 처리
    @Transactional
    public Page<StudentResponse> getHiddenStudents(int page, int size) {

        OneIndexedPageable oneIndexedPageable = UtilService.setOneIndexedPageable(page, size);

        return studentRepository.findByIsEnrolled(oneIndexedPageable, false)
                .map(StudentResponse::new);
    }

    // 학생 상세 조회
    @Transactional
    public StudentResponse getStudent(Long studentId) {

        Student student = studentUtilService.findStudentById(studentId);

        return new StudentResponse(student);
    }

    // 학생 정보 수정
    @Transactional
    public StudentResponse updateStudent(StudentAddRequest request) {

        Student student = studentUtilService.findStudentById(request.getId());
        School school = schoolUtilService.findSchoolByName(request.getSchool());

        student.update(request.getName(), school, request.getGrade(), request.getStudentPhoneNumber(), request.getParentPhoneNumber());

        return new StudentResponse(student);
    }

    // 학생 퇴원 처리
    @Transactional
    public void dischargeStudents(StudentsDischargeRequest request) {

        if (request.getStudentIdList().isEmpty()) {  // 보관할 학생이 없는 경우
            return;
        }

        for (Long id : request.getStudentIdList()) { // 보관할 학생이 있는 경우
            Student student = studentUtilService.findStudentById(id);
            student.updateEnrollStatus(false);
        }
    }

    // 학생 복원 처리
    @Transactional
    public void restoreStudents(StudentsDischargeRequest request) {

        if (request.getStudentIdList().isEmpty()) {  // 복원할 학생이 없는 경우
            return;
        }

        for (Long id : request.getStudentIdList()) { // 복원할 학생이 있는 경우
            Student student = studentUtilService.findStudentById(id);
            student.updateEnrollStatus(true);
        }
    }

    // 학생 정보 삭제 - multiple
    @Transactional
    public void deleteStudents(List<Long> idList) {

        if (idList.isEmpty()) {  // 삭제할 학생이 없는 경우
            return;
        }
        studentRepository.deleteAllById(idList);
    }

    // 학생 검색(이름, 학교, 학년)
    @Transactional
    public Page<StudentResponse> searchStudents
    (List<String> nameList, List<String> schoolList, List<Grade> gradeList, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        OneIndexedPageable oneIndexedPageable = new OneIndexedPageable(pageable);

        Specification<Student> searchFilter = Specification
                .where(StudentSpecifications.nameIn(nameList))
                .and(StudentSpecifications.schoolIn(schoolList))
                .and(StudentSpecifications.gradeIn(gradeList));

        return studentRepository.findAll(searchFilter, oneIndexedPageable).map(StudentResponse::new);
    }

    // 학생 오늘 스케줄 조회
    // ToDo: 클라이언트에 맞춰서 수정 필요
    @Transactional
    public StudentScheduleResponse getStudentTodaySchedule(Long studentId, LocalDate today) {

        StudentBriefResponse studentBrief = getStudentBrief(studentId);

        List<Lecture> lectureList = lectureUtilService.getLectureByDay(today);
        List<LectureBriefResponse> lectureBriefResponseList = lectureList.stream()
                .map(LectureBriefResponse::new)
                .toList();

        List<Task> taskList = taskUtilService.getTaskByStudentAndDate(studentId, today);
        List<TaskBriefResponse> taskBriefResponseList = taskList.stream()
                .map(TaskBriefResponse::new)
                .toList();

        return new StudentScheduleResponse(studentBrief, lectureBriefResponseList, taskBriefResponseList);
    }

    // 학생 간단 조회
    @Transactional
    public StudentBriefResponse getStudentBrief(Long studentId) {

        Student student = studentUtilService.findStudentById(studentId);

        return new StudentBriefResponse(student);
    }

    // 수업별 학생 조회
    @Transactional
    public List<StudentBriefResponse> getStudentsByLecture(Long lectureId) {

        List<Student> studentList = studentUtilService.findStudentsByLectureId(lectureId);

        return studentList.stream()
                .map(StudentBriefResponse::new)
                .toList();
    }

    // 수업 미등록 학생 조회
    // ToDo: 로직 수정 및 최적화 필요
    @Transactional
    public List<StudentResponse> getUnregisteredStudents() {

        LocalDate mon = UtilService.getStartOfWeek(LocalDate.now());
        LocalDate sun = UtilService.getEndOfWeek(LocalDate.now());

        List<School> schoolList = schoolRepository.findByStatus(Status.NON_EXAM);

        List<Student> unregisteredStudents = schoolList.stream()
                .flatMap(school -> school.getStudentList().stream())
                .filter(student -> isStudentUnregistered(student, mon, sun, Status.NON_EXAM))
                .toList();

        return unregisteredStudents.stream()
                .map(StudentResponse::new)
                .collect(Collectors.toList());
    }

    // 수업 미등록 학생 확인
    private boolean isStudentUnregistered(Student student, LocalDate mon, LocalDate sun, Status status) {
        List<Lecture> lectureList = student.getStudentLectureList().stream()
                .map(StudentLecture::getLecture)
                .filter(lecture -> isLectureInCurrentWeek(lecture, mon, sun))
                .toList();

        if (status.equals(Status.NON_EXAM)) {
            boolean isPre = lectureList.stream().anyMatch(lecture -> lecture.getLectureType().getLectureTypeName().equals("PRE"));
            boolean isClass = lectureList.stream().anyMatch(lecture -> lecture.getLectureType().getLectureTypeName().equals("CLASS"));

            return !isPre && !isClass;
        }

        return false;
    }

    // 수업이 현재 주에 있는지 확인
    private boolean isLectureInCurrentWeek(Lecture lecture, LocalDate mon, LocalDate sun) {
        return lecture.getLectureDateList().stream()
                .anyMatch(lectureDate -> !isDateBeforeOrAfter(lectureDate.getLectureDate(), mon, sun));
    }

    // 날짜 비교
    private boolean isDateBeforeOrAfter(LocalDate date, LocalDate start, LocalDate end) {
        return date.isBefore(start) || date.isAfter(end);
    }
}

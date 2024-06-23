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
import com.yangyoung.english.student.dto.request.StudentsSeqUpdateRequest;
import com.yangyoung.english.student.dto.response.StudentBriefResponse;
import com.yangyoung.english.student.dto.response.StudentResponse;
import com.yangyoung.english.student.dto.response.StudentScheduleResponse;
import com.yangyoung.english.student.exception.StudentErrorCode;
import com.yangyoung.english.student.exception.StudentIdDuplicateException;
import com.yangyoung.english.studentLecture.domain.StudentLecture;
import com.yangyoung.english.task.domain.Task;
import com.yangyoung.english.task.dto.response.TaskBriefResponse;
import com.yangyoung.english.task.service.TaskUtilService;
import com.yangyoung.english.util.spreasheet.SheetsService;
import com.yangyoung.english.util.UtilService;
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
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentService {

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

        if (isIdDuplicated(request.getId())) { // id 중복 검사
            StudentErrorCode studentErrorCode = StudentErrorCode.STUDENT_ID_DUPLICATED;
            throw new StudentIdDuplicateException(studentErrorCode, request.getId());
        }

        School school = schoolUtilService.getSchoolByName(request.getSchool());
        Section section = sectionUtilService.findSectionByName(request.getSection());
        Student newStudent = request.toEntity(school, section);
        studentRepository.save(newStudent);

        return new StudentResponse(newStudent);
    }

    // 학생 정보 등록 - 스프레드시트로 등록
    // 매주 금요일 자정마다 실행
    // ToDo : 실행시간 설정 변경
    @Scheduled(cron = "0 0 0 * * FRI")
    @Transactional
    public void addStudentsBySheet() throws Exception {

        List<Student> newStudentList = new ArrayList<>();

        List<List<Object>> studentListData = SheetsService.readSpreadSheet("학생");
        for (List<Object> studentData : studentListData) {
            if (isStudentDataEmpty(studentData)) { // 필수 데이터 확인
                continue;
            }

            Long id = Long.parseLong(studentData.get(0).toString());
            if (isIdDuplicated(id)) { // id 중복 검사
                continue;
            }

            Student newStudent = createStudentFromData(id, studentData);
            newStudentList.add(newStudent);
        }

        studentRepository.saveAll(newStudentList);
    }

    // 아이디 중복 검사
    private boolean isIdDuplicated(Long id) {
        return studentRepository.existsById(id);
    }

    // 필수항목 확인
    private boolean isStudentDataEmpty(List<Object> studentData) { // ToDo : 필수 데이터 기준 수정
        return studentData.get(0).toString().isEmpty() ||
                studentData.get(1).toString().isEmpty() ||
                studentData.get(2).toString().isEmpty();
    }

    // 엑셀 파일에서 읽어온 데이터로 학생 객체 생성
    private Student createStudentFromData(Long id, List<Object> studentData) {
        String name = studentData.get(1).toString();
        Grade grade = Grade.getSecondGradeName((String) studentData.get(2));
        School school = schoolUtilService.getSchoolByName(studentData.get(3).toString());
        Section section = sectionUtilService.findSectionByName(studentData.get(6).toString());

        String studentPhoneNumber = studentData.get(4).toString();
        String parentPhoneNumber = studentData.get(5).toString();

        return Student.builder()
                .id(id)
                .name(name)
                .school(school)
                .grade(grade)
                .section(section)
                .studentPhoneNumber(studentPhoneNumber)
                .parentPhoneNumber(parentPhoneNumber)
                .build();
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
        School school = schoolUtilService.getSchoolByName(request.getSchool());

        student.update(request.getName(), school, request.getGrade(), request.getStudentPhoneNumber(), request.getParentPhoneNumber());
        student.update(request.getName(), school, request.getGrade(), request.getStudentPhoneNumber(), request.getParentPhoneNumber());

        return new StudentResponse(student);
    }

    // 학생 퇴원 처리 - single
    @Transactional
    public void dischargeStudent(Long id) {
        Student student = studentUtilService.findStudentById(id);
        student.updateEnrollStatus(false);
    }

    // 학생 퇴원 처리 - multiple
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

    // 학생 복원 처리 - single
    @Transactional
    public void restoreStudent(Long id) {
        Student student = studentUtilService.findStudentById(id);
        student.updateEnrollStatus(true);
    }

    // 학생 복원 처리 - multiple
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

    // 학생 목록 순서 수정
    @Transactional
    public void updateStudentSequence(StudentsSeqUpdateRequest request) {

        List<Map<Long, Long>> studentIdSeqList = request.getStudentIdSeqList();

        for (Map<Long, Long> studentIdSeq : studentIdSeqList) {
            for (Map.Entry<Long, Long> student : studentIdSeq.entrySet()) {
                Student studentToUpdate = studentUtilService.findStudentById(student.getKey());
                studentToUpdate.updateSequence(student.getValue());
            }
        }
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

    private boolean isLectureInCurrentWeek(Lecture lecture, LocalDate mon, LocalDate sun) {
        return lecture.getLectureDateList().stream()
                .anyMatch(lectureDate -> !isDateBeforeOrAfter(lectureDate.getLectureDate(), mon, sun));
    }

    private boolean isDateBeforeOrAfter(LocalDate date, LocalDate start, LocalDate end) {
        return date.isBefore(start) || date.isAfter(end);
    }

}

package com.yangyoung.english.student.service;

import com.yangyoung.english.configuration.OneIndexedPageable;
import com.yangyoung.english.lecture.domain.Lecture;
import com.yangyoung.english.lecture.dto.response.LectureBriefResponse;
import com.yangyoung.english.lecture.service.LectureUtilService;
import com.yangyoung.english.student.domain.Grade;
import com.yangyoung.english.student.domain.Student;
import com.yangyoung.english.student.domain.StudentRepository;
import com.yangyoung.english.student.dto.request.*;
import com.yangyoung.english.student.dto.response.StudentAddByExcelResponse;
import com.yangyoung.english.student.dto.response.StudentBriefResponse;
import com.yangyoung.english.student.dto.response.StudentResponse;
import com.yangyoung.english.student.dto.response.StudentScheduleResponse;
import com.yangyoung.english.student.exception.StudentErrorCode;
import com.yangyoung.english.student.exception.StudentIdDuplicateException;
import com.yangyoung.english.task.domain.Task;
import com.yangyoung.english.task.dto.response.TaskBriefResponse;
import com.yangyoung.english.task.service.TaskUtilService;
import com.yangyoung.english.util.UtilService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentUtilService studentUtilService;
    private final LectureUtilService lectureUtilService;
    private final TaskUtilService taskUtilService;
    private final UtilService utilService;

    // 학생 정보 등록 - 폼 입력으로 등록
    @Transactional
    public StudentResponse addStudentByForm(StudentRequest request) {

        checkIdDuplicated(request.getId());

        Student newStudent = request.toEntity();
        studentRepository.save(newStudent);

        return new StudentResponse(newStudent);
    }

    // 학생 정보 등록 - 엑셀 파일로 등록
    @Transactional
    public StudentAddByExcelResponse addStudentsByExcel(StudentAddByExcelRequest request) throws Exception {

        List<Student> newStudentList = new ArrayList<>();
        List<StudentResponse> newStudentResponseList = new ArrayList<>();

        List<List<Object>> studentListData = utilService.readExcel(request.getFile(), request.getSheetType());

        for (List<Object> studentData : studentListData) {  // 엑셀 파일에서 읽어온 데이터를 하나씩 확인

            if (isStudentDataEmpty(studentData)) { // 필수항목이 비어있는 경우
                continue;
            }

            Long id = Long.parseLong(studentData.get(0).toString());
            String name = studentData.get(1).toString();
            Grade grade = utilService.getGrade((String) studentData.get(2));
            String school = studentData.get(3).toString();
            String studentPhoneNumber = studentData.get(4).toString();
            String parentPhoneNumber = studentData.get(5).toString();

            checkIdDuplicated(id);

            Student newStudent = Student.builder()
                    .id(id)
                    .name(name)
                    .school(school)
                    .grade(grade)
                    .studentPhoneNumber(studentPhoneNumber)
                    .parentPhoneNumber(parentPhoneNumber)
                    .build();
            newStudentList.add(newStudent);
            newStudentResponseList.add(new StudentResponse(newStudent));
        }
        studentRepository.saveAll(newStudentList);

        return new StudentAddByExcelResponse(newStudentResponseList, newStudentList.size());
    }

    // 아이디 중복 검사
    private void checkIdDuplicated(Long id) {

        boolean isIdDuplicated = studentRepository.existsById(id);
        if (isIdDuplicated) {  // 아이디 중복 검사
            StudentErrorCode studentErrorCode = StudentErrorCode.STUDENT_ID_DUPLICATED;
            throw new StudentIdDuplicateException(studentErrorCode, id);
        }
    }

    // 필수항목 확인
    private boolean isStudentDataEmpty(List<Object> studentData) {
        return studentData.get(0).toString().isEmpty() || studentData.get(1).toString().isEmpty() || studentData.get(2).toString().isEmpty();
    }

    // 학생 전체 조회 - 페이징 처리
    @Transactional
    public Page<StudentResponse> getAllStudents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        OneIndexedPageable oneIndexedPageable = new OneIndexedPageable(pageable);

        return studentRepository.findByIsEnrolled(oneIndexedPageable, true)
                .map(StudentResponse::new);
    }

    // 숨김 학생 전체 조회 - 페이징 처리
    @Transactional
    public Page<StudentResponse> getHiddenStudents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        OneIndexedPageable oneIndexedPageable = new OneIndexedPageable(pageable);

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
    public StudentResponse updateStudent(StudentRequest request) {

        Student student = studentUtilService.findStudentById(request.getId());

        student.update(request.getName(), request.getSchool(), request.getGrade(), request.getStudentPhoneNumber(), request.getParentPhoneNumber());

        return new StudentResponse(student);
    }

    // 학생 정보 보관 - single
    @Transactional
    public void dischargeStudent(Long id) {
        Student student = studentUtilService.findStudentById(id);
        student.updateEnrollStatus();
    }

    // 학생 정보 보관 - multiple
    @Transactional
    public void dischargeStudents(StudentsDischargeRequest request) {

        if (request.getStudentIdList().isEmpty()) {  // 보관할 학생이 없는 경우
            return;
        }

        for (Long id : request.getStudentIdList()) { // 보관할 학생이 있는 경우
            Student student = studentUtilService.findStudentById(id);
            student.updateEnrollStatus();
        }
    }

    // 학생 정보 삭제 - single
    @Transactional
    public void deleteStudent(Long id) {
        Student student = studentUtilService.findStudentById(id);

        studentRepository.delete(student);
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
    public Page<StudentResponse> searchStudents(List<String> nameList, List<String> schoolList, List<Grade> gradeList, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        OneIndexedPageable oneIndexedPageable = new OneIndexedPageable(pageable);

        Specification<Student> searchFilter = Specification
                .where(StudentSpecifications.nameIn(nameList))
                .and(StudentSpecifications.schoolIn(schoolList))
                .and(StudentSpecifications.gradeIn(gradeList));

        List<Student> studentList = studentRepository.findAll(searchFilter);
        log.info("search size: {}", studentList.size());
        studentList.forEach(student -> log.info("List Student name: {}", student.getName()));

//        Page<Student> searchResult = studentRepository.findAll(searchFilter, oneIndexedPageable);
//        log.info("page size: {}", searchResult.getTotalElements());
//        searchResult.forEach(student -> log.info("Page Student name: {}", student.getName()));

        return studentRepository.findAll(searchFilter, oneIndexedPageable).map(StudentResponse::new);
    }

    // 학생 오늘 스케줄 조회
    @Transactional
    public StudentScheduleResponse getStudentTodaySchedule(Long studentId, LocalDate today) {

        StudentBriefResponse studentBrief = getStudentBrief(studentId);

        List<Lecture> lectureList = lectureUtilService.getLectureBriefByDay(today);
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

    // 특정 강의 수강하는 학생 조회
    @Transactional
    public List<StudentBriefResponse> getStudentsByLecture(Long lectureId) {

        List<Student> studentList = studentUtilService.findStudentsByLectureId(lectureId);

        return studentList.stream()
                .map(StudentBriefResponse::new)
                .toList();
    }
}

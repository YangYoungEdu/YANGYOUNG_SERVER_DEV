package com.yangyoung.english.student.service;

import com.yangyoung.english.student.domain.Grade;
import com.yangyoung.english.student.domain.Student;
import com.yangyoung.english.student.domain.StudentRepository;
import com.yangyoung.english.student.dto.request.StudentAddByExcelRequest;
import com.yangyoung.english.student.dto.request.StudentRequest;
import com.yangyoung.english.student.dto.request.StudentsDischargeRequest;
import com.yangyoung.english.student.dto.request.StudentsSeqUpdateRequest;
import com.yangyoung.english.student.dto.response.StudentAddByExcelResponse;
import com.yangyoung.english.student.dto.response.StudentResponse;
import com.yangyoung.english.student.exception.StudentErrorCode;
import com.yangyoung.english.student.exception.StudentIdDuplicateException;
import com.yangyoung.english.util.UtilService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentUtilService studentUtilService;
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
        return studentRepository.findAll(pageable).map(StudentResponse::new);
    }

    // 학생 상세 조회
    @Transactional
    public StudentResponse getStudent(Long id) {

        Student student = studentUtilService.findStudentById(id);

        return new StudentResponse(student);
    }

    // 학생 정보 수정
    @Transactional
    public StudentResponse updateStudent(StudentRequest request) {

        Student student = studentUtilService.findStudentById(request.getId());

        student.update(request.getName(), request.getSchool(), request.getGrade(), request.getStudentPhoneNumber(), request.getParentPhoneNumber());
        studentRepository.save(student);

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
}

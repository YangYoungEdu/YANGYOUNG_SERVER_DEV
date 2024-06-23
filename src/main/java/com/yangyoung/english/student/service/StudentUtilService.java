package com.yangyoung.english.student.service;

import com.yangyoung.english.student.domain.Student;
import com.yangyoung.english.student.domain.StudentRepository;
import com.yangyoung.english.student.exception.StudentErrorCode;
import com.yangyoung.english.student.exception.StudentNotFoundException;
import com.yangyoung.english.studentLecture.domain.StudentLectureRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentUtilService {

    private final StudentRepository studentRepository;
    private final StudentLectureRepository studentLectureRepository;

    // id로 학생 정보 조회
    @Transactional
    public Student findStudentById(Long id) {
        Optional<Student> student = studentRepository.findById(id);
        if (student.isEmpty()) {
            StudentErrorCode studentErrorCode = StudentErrorCode.STUDENT_NOT_FOUND;
            throw new StudentNotFoundException(studentErrorCode, id);
        }

        return student.get();
    }

    // 이름으로 학생 정보 조회
    @Transactional
    public Student findStudentByName(String name) {
        Optional<Student> student = studentRepository.findByName(name);
        if (student.isEmpty()) {
            StudentErrorCode studentErrorCode = StudentErrorCode.STUDENT_NOT_FOUND;
            throw new StudentNotFoundException(studentErrorCode, name);
        }

        return student.get();
    }

    // 이름, 학교로 학생 정보 조회
    @Transactional
    public Student findStudentByNameAndSchool(String name, String school) {
        Optional<Student> student = studentRepository.findByNameAndSchoolName(name, school);
        if (student.isEmpty()) {
            StudentErrorCode studentErrorCode = StudentErrorCode.STUDENT_NOT_FOUND;
            throw new StudentNotFoundException(studentErrorCode, name);
        }

        return student.get();
    }

    @Transactional
    public Optional<Student> findStudentByNameAndSchoolOptional(String name, String school) {
        return studentRepository.findByNameAndSchoolName(name, school);
    }

    // 특정 강의 수강하는 학생 조회
    @Transactional
    public List<Student> findStudentsByLectureId(Long lectureId) {
        return studentLectureRepository.findStudentsByLectureId(lectureId);
    }
}

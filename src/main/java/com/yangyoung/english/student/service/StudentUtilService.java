package com.yangyoung.english.student.service;

import com.yangyoung.english.student.domain.Student;
import com.yangyoung.english.student.domain.StudentRepository;
import com.yangyoung.english.student.exception.StudentErrorCode;
import com.yangyoung.english.student.exception.StudentNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentUtilService {

    private final StudentRepository studentRepository;

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
}

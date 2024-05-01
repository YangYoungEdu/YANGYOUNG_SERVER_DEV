package com.yangyoung.english.student.service;

import com.yangyoung.english.student.domain.Student;
import com.yangyoung.english.student.domain.StudentRepository;
import com.yangyoung.english.student.exception.StudentNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentUtilService {

    private final StudentRepository studentRepository;
    private final Logger logger = LoggerFactory.getLogger(StudentService.class);
    private final static String STUDENT_NOT_EXIST_MESSAGE = "Student does not exist. (studentId: %d)";

    // id로 학생 정보 조회
    @Transactional
    public Student findStudentById(Long id) {
        Optional<Student> student = studentRepository.findById(id);
        if (student.isEmpty()) {
            String errorMessage = String.format(STUDENT_NOT_EXIST_MESSAGE, id);
            logger.error(errorMessage);
            throw new StudentNotFoundException(errorMessage);
        }

        return student.get();
    }
}

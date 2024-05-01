package com.yangyoung.english.student;

import com.yangyoung.english.student.domain.StudentRepository;
import com.yangyoung.english.student.service.StudentService;
import com.yangyoung.english.util.UtilService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private UtilService utilService;

    @InjectMocks
    private StudentService studentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
}

package com.yangyoung.english.student;

import com.yangyoung.english.student.domain.Grade;
import com.yangyoung.english.student.domain.Student;
import com.yangyoung.english.student.domain.StudentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @Test
    @DisplayName("학생 저장 테스트")
    void saveStudent() {
        // given
        Student student = Student.builder()
                .id(1L)
                .name("홍길동")
                .school("양영학원")
                .grade(Grade.H1)
                .studentPhoneNumber("010-1234-5678")
                .parentPhoneNumber("010-1234-5678")
                .build();

        // when
        Student savedStudent = studentRepository.save(student);

        // then
        assertThat(savedStudent.getId()).isNotNull();
        assertThat(savedStudent.getName()).isEqualTo("홍길동");
        assertThat(savedStudent.getSchool()).isEqualTo("양영학원");
        assertThat(savedStudent.getGrade()).isEqualTo(Grade.H1);
        assertThat(savedStudent.getStudentPhoneNumber()).isEqualTo("010-1234-5678");
        assertThat(savedStudent.getParentPhoneNumber()).isEqualTo("010-1234-5678");
    }

    @Test
    @DisplayName("학생 저장 테스트 - null 값")
    void saveStudentWithNull() {
        // given
        Student student = Student.builder()
                .id(1L)
                .name("홍길동")
                .school("양영학원")
                .grade(Grade.H1)
                .studentPhoneNumber(null)
                .parentPhoneNumber(null)
                .build();

        // when
        Student savedStudent = studentRepository.save(student);

        // then
        assertThat(savedStudent.getId()).isNotNull();
        assertThat(savedStudent.getName()).isEqualTo("홍길동");
        assertThat(savedStudent.getSchool()).isEqualTo("양영학원");
        assertThat(savedStudent.getGrade()).isEqualTo(Grade.H1);
        assertThat(savedStudent.getStudentPhoneNumber()).isNull();
        assertThat(savedStudent.getParentPhoneNumber()).isNull();
    }

    @Test
    @DisplayName("학생 전체 조회 - 페이징 처리")
    void getAllStudentsPaged() {
        // given
        Student student1 = Student.builder()
                .id(1L)
                .name("홍길동")
                .school("양영학원")
                .grade(Grade.H1)
                .studentPhoneNumber("010-1234-5678")
                .parentPhoneNumber("010-1234-5678")
                .build();

        Student student2 = Student.builder()
                .id(2L)
                .name("김철수")
                .school("양영학원")
                .grade(Grade.H2)
                .studentPhoneNumber("010-1234-5678")
                .parentPhoneNumber("010-1234-5678")
                .build();

        studentRepository.save(student1);
        studentRepository.save(student2);

        // when
        Page<Student> studentsPage = studentRepository.findAll(PageRequest.of(0, 10));

        // then
        assertThat(studentsPage.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("학생 단건 조회")
    void getStudent() {
        // given
        Student student = Student.builder()
                .id(1L)
                .name("홍길동")
                .school("양영학원")
                .grade(Grade.H1)
                .studentPhoneNumber("010-1234-5678")
                .parentPhoneNumber("010-1234-5678")
                .build();

        Student savedStudent = studentRepository.save(student);

        // when
        Student foundStudent = studentRepository.findById(savedStudent.getId()).orElse(null);

        // then
        assertThat(foundStudent).isNotNull();
        if (foundStudent != null) {
            assertThat(foundStudent.getId()).isEqualTo(savedStudent.getId());
            assertThat(foundStudent.getName()).isEqualTo("홍길동");
            assertThat(foundStudent.getSchool()).isEqualTo("양영학원");
            assertThat(foundStudent.getGrade()).isEqualTo(Grade.H1);
            assertThat(foundStudent.getStudentPhoneNumber()).isEqualTo("010-1234-5678");
            assertThat(foundStudent.getParentPhoneNumber()).isEqualTo("010-1234-5678");
        }
    }

    @Test
    @DisplayName("학생 정보 수정")
    void updateStudent() {
        // given
        Student student = Student.builder()
                .id(1L)
                .name("홍길동")
                .school("양영학원")
                .grade(Grade.H1)
                .studentPhoneNumber("010-1234-5678")
                .parentPhoneNumber("010-1234-5678")
                .build();

        Student savedStudent = studentRepository.save(student);

        // when
        savedStudent.update("김철수", "양영학원", Grade.H2, "010-5678-1234", "010-5678-1234");

        // then
        Student updatedStudent = studentRepository.findById(savedStudent.getId()).orElse(null);
        assertThat(updatedStudent).isNotNull();
        if (updatedStudent != null) {
            assertThat(updatedStudent.getId()).isEqualTo(savedStudent.getId());
            assertThat(updatedStudent.getName()).isEqualTo("김철수");
            assertThat(updatedStudent.getSchool()).isEqualTo("양영학원");
            assertThat(updatedStudent.getGrade()).isEqualTo(Grade.H2);
            assertThat(updatedStudent.getStudentPhoneNumber()).isEqualTo("010-5678-1234");
            assertThat(updatedStudent.getParentPhoneNumber()).isEqualTo("010-5678-1234");
        }
    }

    @Test
    @DisplayName("학생 정보 삭제 - single")
    void deleteStudent() {
        // given
        Student student = Student.builder()
                .id(1L)
                .name("홍길동")
                .school("양영학원")
                .grade(Grade.H1)
                .studentPhoneNumber("010-1234-5678")
                .parentPhoneNumber("010-1234-5678")
                .build();

        Student savedStudent = studentRepository.save(student);

        // when
        studentRepository.delete(savedStudent);

        // then
        Student deletedStudent = studentRepository.findById(savedStudent.getId()).orElse(null);
        assertThat(deletedStudent).isNull();
    }

    @Test
    @DisplayName("학생 정보 삭제 - multiple")
    void deleteStudents() {
        // given
        Student student1 = Student.builder()
                .id(1L)
                .name("홍길동")
                .school("양영학원")
                .grade(Grade.H1)
                .studentPhoneNumber("010-1234-5678")
                .parentPhoneNumber("010-1234-5678")
                .build();

        Student student2 = Student.builder()
                .id(2L)
                .name("김철수")
                .school("양영학원")
                .grade(Grade.H2)
                .studentPhoneNumber("010-1234-5678")
                .parentPhoneNumber("010-1234-5678")
                .build();

        studentRepository.save(student1);
        studentRepository.save(student2);

        // when
        studentRepository.deleteAll();

        // then
        assertThat(studentRepository.findAll().size()).isEqualTo(0);
    }
}

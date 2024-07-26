package com.yangyoung.english.student.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {

    @Query("SELECT s FROM Student s WHERE s.isEnrolled = :isEnrolled")
    Page<Student> findByIsEnrolled(Pageable pageable, boolean isEnrolled);

//    Page<Student> findByIsEnrolled(Specification<Student> spec, Pageable pageable, boolean isEnrolled);

    Optional<Student> findByName(String name);

    // 이름과 학교로 검색
    @Query("SELECT s FROM Student s WHERE s.name = :name AND s.school.name = :schoolName")
    Optional<Student> findByNameAndSchoolName(@Param("name") String name, @Param("schoolName") String schoolName);

    List<Student> findByIsLectureRegisteredFalse();
}

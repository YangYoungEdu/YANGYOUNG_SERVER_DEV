package com.yangyoung.english.student.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query("SELECT s FROM Student s WHERE s.isEnrolled = :isEnrolled")
    Page<Student> findByIsEnrolled(Pageable pageable, boolean isEnrolled);

    Page<Student> findByIsEnrolledTrue(Pageable pageable);


}

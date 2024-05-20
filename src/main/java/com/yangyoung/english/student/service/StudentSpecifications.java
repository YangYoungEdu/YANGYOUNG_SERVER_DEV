package com.yangyoung.english.student.service;

import org.springframework.data.jpa.domain.Specification;
import com.yangyoung.english.student.domain.Student;
import com.yangyoung.english.student.domain.Grade;

import java.util.List;

public class StudentSpecifications {

    public static Specification<Student> nameIn(List<String> names) {
        return (root, query, criteriaBuilder) -> {
            if (names == null || names.isEmpty()) {
                return criteriaBuilder.conjunction(); // always true
            }
            return root.get("name").in(names);
        };
    }

    public static Specification<Student> schoolIn(List<String> schools) {
        return (root, query, criteriaBuilder) -> {
            if (schools == null || schools.isEmpty()) {
                return criteriaBuilder.conjunction(); // always true
            }
            return root.get("school").in(schools);
        };
    }

    public static Specification<Student> gradeIn(List<Grade> grades) {
        return (root, query, criteriaBuilder) -> {
            if (grades == null || grades.isEmpty()) {
                return criteriaBuilder.conjunction(); // always true
            }
            return root.get("grade").in(grades);
        };
    }
}


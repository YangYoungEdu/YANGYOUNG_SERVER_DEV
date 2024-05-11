package com.yangyoung.english.lectureTask.domain;

import com.yangyoung.english.task.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LectureTaskRepository extends JpaRepository<LectureTask, Long> {

    // lectureId로 Task 조회
    @Query("SELECT lt.task FROM LectureTask lt WHERE lt.lecture.id = :lectureId")
    List<Task> findByLectureId(Long lectureId);
}

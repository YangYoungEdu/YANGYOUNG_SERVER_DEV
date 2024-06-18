package com.yangyoung.english.lectureTask.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.yangyoung.english.lecture.domain.Lecture;
import com.yangyoung.english.task.domain.Task;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LectureTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "lecture_id")
    @JsonManagedReference
    private Lecture lecture;

    @ManyToOne()
    @JoinColumn(name = "task_id")
    @JsonManagedReference
    private Task task;

    @Builder
    public LectureTask(Lecture lecture, Task task) {
        this.lecture = lecture;
        this.task = task;
    }
}

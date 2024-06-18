package com.yangyoung.english.task.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.yangyoung.english.lectureTask.domain.LectureTask;
import com.yangyoung.english.studentTask.domain.TaskProgress;
import com.yangyoung.english.studentTask.domain.StudentTask;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.bytebuddy.asm.Advice;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @Enumerated(EnumType.STRING)
    private TaskType taskType;

    private String lectureName;

    private LocalDate taskDate;

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<StudentTask> studentTaskList;

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<LectureTask> lectureTaskList;

    //학생 과제
    @Builder
    public Task(String content, TaskType taskType, LocalDate taskDate) {
        this.content = content;
        this.taskType = taskType;
        this.taskDate = taskDate;
    }

    // 강의 과제
    @Builder
    public Task(String content, TaskType taskType, String lectureName, LocalDate taskDate) {
        this.content = content;
        this.taskType = taskType;
        this.lectureName = lectureName;
        this.taskDate = taskDate;
    }

    public void update(String content, LocalDate taskDate) {
        if (!content.isBlank()) {
            this.content = content;
        }
        if (taskDate != null) {
            this.taskDate = taskDate;
        }
    }
}

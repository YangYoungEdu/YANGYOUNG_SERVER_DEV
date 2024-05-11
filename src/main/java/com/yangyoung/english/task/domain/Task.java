package com.yangyoung.english.task.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.yangyoung.english.lectureTask.domain.LectureTask;
import com.yangyoung.english.studentTask.domain.TaskProgress;
import com.yangyoung.english.studentTask.domain.StudentTask;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @Enumerated(EnumType.STRING)
    private TaskType taskType;

    private LocalDate taskDate;

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<StudentTask> studentTaskList;

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<LectureTask> lectureTaskList;

    @Builder
    public Task(String content, TaskType taskType, LocalDate taskDate) {
        this.content = content;
        this.taskType = taskType;
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

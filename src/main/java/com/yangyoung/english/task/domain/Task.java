package com.yangyoung.english.task.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.yangyoung.english.lectureTask.domain.LectureTask;
import com.yangyoung.english.studentTask.domain.StudentTask;
import jakarta.persistence.*;
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

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskType taskType;

    @Column(nullable = false)
    private LocalDate taskDate;

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonBackReference
    private List<StudentTask> studentTaskList;

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonBackReference
    private List<LectureTask> lectureTaskList;
}

package com.yangyoung.english.studentTask.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.yangyoung.english.student.domain.Student;
import com.yangyoung.english.task.domain.Task;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class StudentTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "student_id")
    @JsonManagedReference
    private Student student;

    @ManyToOne()
    @JoinColumn(name = "task_id")
    @JsonManagedReference
    private Task task;

    @Enumerated(EnumType.STRING)
    private TaskProgress taskProgress;

    @Builder
    public StudentTask(Student student, Task task) {
        this.student = student;
        this.task = task;
        this.taskProgress = TaskProgress.NOT_STARTED;
    }

    public void updateTaskProgress(TaskProgress taskProgress) {
        this.taskProgress = taskProgress;
    }
}

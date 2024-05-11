package com.yangyoung.english.task.dto.response;

import com.yangyoung.english.student.domain.Student;
import com.yangyoung.english.studentTask.domain.StudentTask;
import com.yangyoung.english.task.domain.Task;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StudentTaskResponse {

    private Long id;

    private String content;

    private String taskType;

    private String taskDate;

    private String taskProgress;

    public StudentTaskResponse(Student student, Task task) {
        this.id = task.getId();
        this.content = task.getContent();
        this.taskType = task.getTaskType().name();
        this.taskDate = task.getTaskDate().toString();
    }

    public StudentTaskResponse(StudentTask studentTask) {
        this.id = studentTask.getTask().getId();
        this.content = studentTask.getTask().getContent();
        this.taskType = studentTask.getTask().getTaskType().name();
        this.taskDate = studentTask.getTask().getTaskDate().toString();
        this.taskProgress = studentTask.getTaskProgress().name();
    }
}
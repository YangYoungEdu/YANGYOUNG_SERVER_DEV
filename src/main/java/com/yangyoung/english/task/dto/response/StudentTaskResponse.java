package com.yangyoung.english.task.dto.response;

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

    private String lectureName;

    private String taskType;

    private String taskDate;

    private String taskProgress;

    public StudentTaskResponse(Task task) {
        this.id = task.getId();
        this.content = task.getContent();
        this.taskType = task.getTaskType().getTypeName();
        this.taskDate = task.getTaskDate().toString();
    }

    public StudentTaskResponse(StudentTask studentTask) {
        this.id = studentTask.getTask().getId();
        this.content = studentTask.getTask().getContent();
        this.lectureName = studentTask.getTask().getLectureName();
        this.taskType = studentTask.getTask().getTaskType().getTypeName();
        this.taskDate = studentTask.getTask().getTaskDate().toString();
        this.taskProgress = studentTask.getTaskProgress().getProgressName();
    }
}

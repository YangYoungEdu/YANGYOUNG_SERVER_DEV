package com.yangyoung.english.task.dto.response;

import com.yangyoung.english.task.domain.Task;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {

    private Long id;

    private String content;

    private String taskType;

    private String taskDate;

    public TaskResponse(Task task) {
        this.id = task.getId();
        this.content = task.getContent();
        this.taskType = task.getTaskType().getTypeName();
        this.taskDate = task.getTaskDate().toString();
    }

}

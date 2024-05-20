package com.yangyoung.english.task.dto.response;

import com.yangyoung.english.task.domain.Task;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LectureTaskResponse {

    private Long id;

    private String content;

    private String lectureName;

    private String taskDate;

    private String taskType;

    public LectureTaskResponse(Task task) {
        this.id = task.getId();
        this.content = task.getContent();
        this.lectureName = task.getLectureName();
        this.taskDate = task.getTaskDate().toString();
        this.taskType = task.getTaskType().name();
    }
}

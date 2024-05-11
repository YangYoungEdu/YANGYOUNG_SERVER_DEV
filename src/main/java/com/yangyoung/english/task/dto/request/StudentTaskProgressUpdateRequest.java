package com.yangyoung.english.task.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StudentTaskProgressUpdateRequest {

    private Long studentId;

    private Long taskId;

    private String taskProgress;

    public StudentTaskProgressUpdateRequest(Long studentId, Long taskId, String taskProgress) {
        this.studentId = studentId;
        this.taskId = taskId;
        this.taskProgress = taskProgress;
    }
}

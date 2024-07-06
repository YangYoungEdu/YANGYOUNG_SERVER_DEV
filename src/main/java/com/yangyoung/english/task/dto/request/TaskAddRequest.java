package com.yangyoung.english.task.dto.request;

import com.yangyoung.english.task.domain.Task;
import com.yangyoung.english.task.domain.TaskType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class TaskAddRequest {

    private Long studentId;

    private String content;

    private Long lectureId;

    private String taskType;

    private LocalDate taskDate;

    public Task toEntity() {
        return Task.builder()
                .content(content)
                .taskType(TaskType.getTaskType(taskType))
                .taskDate(taskDate)
                .build();
    }
}

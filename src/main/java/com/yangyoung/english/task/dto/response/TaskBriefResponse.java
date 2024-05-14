package com.yangyoung.english.task.dto.response;

import com.yangyoung.english.task.domain.Task;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TaskBriefResponse {

    private String content;

    public TaskBriefResponse(Task task) {
        this.content = task.getContent();
    }
}

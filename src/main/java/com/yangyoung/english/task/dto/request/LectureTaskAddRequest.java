package com.yangyoung.english.task.dto.request;

import com.yangyoung.english.lecture.domain.Lecture;
import com.yangyoung.english.task.domain.Task;
import com.yangyoung.english.task.domain.TaskType;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class LectureTaskAddRequest {

    private Long lectureId;

    private String content;

    private LocalDate taskDate;

    public Task toEntity(Lecture lecture) {
        return new Task(content, TaskType.LECTURE, lecture.getName(), taskDate);
    }
}

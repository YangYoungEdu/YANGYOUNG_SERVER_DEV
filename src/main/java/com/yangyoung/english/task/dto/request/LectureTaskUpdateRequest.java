package com.yangyoung.english.task.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class LectureTaskUpdateRequest {

    private Long lectureId;

    private Long taskId;

    private String content;

    private LocalDate taskDate;
}

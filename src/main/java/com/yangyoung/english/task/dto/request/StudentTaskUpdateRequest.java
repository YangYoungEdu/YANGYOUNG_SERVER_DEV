package com.yangyoung.english.task.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class StudentTaskUpdateRequest {

    private Long studentId;

    private Long taskId;

    private String content;

    private LocalDate taskDate;
}

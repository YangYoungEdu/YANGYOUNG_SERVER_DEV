package com.yangyoung.english.student.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class StudentsDischargeRequest {

    private List<Long> studentIdList;
}

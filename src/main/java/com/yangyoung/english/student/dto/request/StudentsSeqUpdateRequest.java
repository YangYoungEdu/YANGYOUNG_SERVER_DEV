package com.yangyoung.english.student.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class StudentsSeqUpdateRequest {

    private List<Map<Long, Long>> studentIdSeqList;
}

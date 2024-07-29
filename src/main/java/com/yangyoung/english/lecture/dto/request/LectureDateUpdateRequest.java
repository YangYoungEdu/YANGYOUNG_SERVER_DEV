package com.yangyoung.english.lecture.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
public class LectureDateUpdateRequest {

    private Long id;

    private List<LocalDate> updatedLectureDateList;
}

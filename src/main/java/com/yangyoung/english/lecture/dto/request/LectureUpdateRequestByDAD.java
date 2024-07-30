package com.yangyoung.english.lecture.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class LectureUpdateRequestByDAD {

    private Long id;

    private LocalTime startTime;

    private LocalTime endTime;

    private LocalDate updatedLectureDate;
}

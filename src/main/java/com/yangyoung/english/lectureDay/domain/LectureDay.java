package com.yangyoung.english.lectureDay.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.yangyoung.english.lecture.domain.Lecture;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;

@Entity
@Getter
@NoArgsConstructor
public class LectureDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private DayOfWeek lectureDay;

    @ManyToOne()
    @JoinColumn(name = "lecture_id")
    @JsonManagedReference
    private Lecture lecture;

    @Builder
    public LectureDay(DayOfWeek lectureDay, Lecture lecture) {
        this.lectureDay = lectureDay;
        this.lecture = lecture;
    }

    public String getLectureDay() {
        return
                switch (lectureDay) {
                    case MONDAY -> "월요일";
                    case TUESDAY -> "화요일";
                    case WEDNESDAY -> "수요일";
                    case THURSDAY -> "목요일";
                    case FRIDAY -> "금요일";
                    case SATURDAY -> "토요일";
                    case SUNDAY -> "일요일";
                };
    }
}

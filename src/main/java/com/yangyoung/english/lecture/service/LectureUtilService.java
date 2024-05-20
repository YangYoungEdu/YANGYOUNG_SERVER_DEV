package com.yangyoung.english.lecture.service;

import com.yangyoung.english.lecture.domain.Lecture;
import com.yangyoung.english.lecture.domain.LectureRepository;
import com.yangyoung.english.lecture.dto.response.LectureBriefResponse;
import com.yangyoung.english.lecture.exception.LectureErrorCode;
import com.yangyoung.english.lecture.exception.LectureNotFoundException;
import com.yangyoung.english.studentLecture.domain.StudentLectureRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LectureUtilService {

    private final LectureRepository lectureRepository;
    private final StudentLectureRepository studentLectureRepository;


    // 강의 ID로 강의 조회
    @Transactional
    public Lecture findLectureById(Long lectureId) {
        Optional<Lecture> lecture = lectureRepository.findById(lectureId);
        if (lecture.isEmpty()) {
            LectureErrorCode lectureErrorCode = LectureErrorCode.LECTURE_NOT_FOUND;
            throw new LectureNotFoundException(lectureErrorCode, lectureId);
        }
        return lecture.get();
    }

    // 강의 간단 조회 - 일단위
    @Transactional
    public List<Lecture> getLectureBriefByDay(LocalDate date) {
        return lectureRepository.findLecturesByDate(date);
    }

    // 특정 학생이 수강하는 강의 조회
    @Transactional
    public List<Lecture> findLecturesByStudentId(Long studentId) {
        return studentLectureRepository.findLecturesByStudentId(studentId);
    }
}

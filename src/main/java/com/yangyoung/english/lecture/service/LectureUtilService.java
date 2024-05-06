package com.yangyoung.english.lecture.service;

import com.yangyoung.english.lecture.domain.Lecture;
import com.yangyoung.english.lecture.domain.LectureRepository;
import com.yangyoung.english.lecture.exception.LectureNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LectureUtilService {

    private final static String LECTURE_NOT_FOUND_MESSAGE = "Lecture not found. (lectureId: %d)";
    private final LectureRepository lectureRepository;
    private final Logger logger = LoggerFactory.getLogger(LectureUtilService.class);

    // 강의 ID로 강의 조회
    @Transactional
    public Lecture findLectureById(Long lectureId) {
        Optional<Lecture> lecture = lectureRepository.findById(lectureId);
        if (lecture.isEmpty()) {
            String errorMessage = String.format(LECTURE_NOT_FOUND_MESSAGE, lectureId);
            logger.error(errorMessage);
            throw new LectureNotFoundException(errorMessage);
        }
        return lecture.get();
    }
}

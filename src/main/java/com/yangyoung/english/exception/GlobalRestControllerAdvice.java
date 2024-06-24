package com.yangyoung.english.exception;

import com.yangyoung.english.appUser.exception.InvalidTokenException;
import com.yangyoung.english.appUser.exception.PasswordNotMatchException;
import com.yangyoung.english.appUser.exception.UserNotFoundException;
import com.yangyoung.english.lecture.exception.LectureNameDuplicateException;
import com.yangyoung.english.lecture.exception.LectureNotFoundException;
import com.yangyoung.english.student.exception.StudentIdDuplicateException;
import com.yangyoung.english.student.exception.StudentNotFoundException;
import com.yangyoung.english.task.exception.TaskNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice()
public class GlobalRestControllerAdvice {

    private final Logger logger = LoggerFactory.getLogger(GlobalRestControllerAdvice.class);

    // 강의 이름 중복 예외 처리
    @ExceptionHandler(LectureNameDuplicateException.class)
    public ResponseEntity<ErrorResponse> handleLectureNameDuplicateException(LectureNameDuplicateException e) {
        ErrorResponse errorresponse = new ErrorResponse(e.lectureErrorCode.name(), e.getLectureErrorCode().getHttpStatus(), e.getMessage());
        logger.error("LectureNameDuplicateException: {}", errorresponse.getMessage());
        return ResponseEntity.status(e.getLectureErrorCode().getHttpStatus()).body(errorresponse);
    }

    // 강의 조회 예외 처리 - 강의가 존재하지 않을 때
    @ExceptionHandler(LectureNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleLectureNotFoundException(LectureNotFoundException e) {
        ErrorResponse errorresponse = new ErrorResponse(e.getLectureErrorCode().name(), e.getLectureErrorCode().getHttpStatus(), e.getMessage());
        logger.error("LectureNotFoundException: {}", errorresponse.getMessage());
        return ResponseEntity.status(e.getLectureErrorCode().getHttpStatus()).body(errorresponse);
    }

    // 학생 ID 중복 예외 처리
    @ExceptionHandler(StudentIdDuplicateException.class)
    public ResponseEntity<ErrorResponse> handleStudentIdDuplicateException(StudentIdDuplicateException e) {
        ErrorResponse errorresponse = new ErrorResponse(e.getStudentErrorCode().name(), e.getStudentErrorCode().getHttpStatus(), e.getMessage());
        logger.error("StudentIdDuplicateException: {}", errorresponse.getMessage());
        return ResponseEntity.status(e.getStudentErrorCode().getHttpStatus()).body(errorresponse);
    }

    // 학생 조회 예외 처리 - 학생이 존재하지 않을 때
    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleStudentNotFoundException(StudentNotFoundException e) {
        ErrorResponse errorresponse = new ErrorResponse(e.getStudentErrorCode().name(), e.getStudentErrorCode().getHttpStatus(), e.getMessage());
        logger.error("StudentNotFoundException: {}", errorresponse.getMessage());
        return ResponseEntity.status(e.getStudentErrorCode().getHttpStatus()).body(errorresponse);
    }

    // 학생 과제 조회 예외 처리 - 과제가 존재하지 않을 때
    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTaskNotFoundException(TaskNotFoundException e) {
        ErrorResponse errorresponse = new ErrorResponse(e.getTaskErrorCode().name(), e.getTaskErrorCode().getHttpStatus(), e.getMessage());
        logger.error("TaskNotFoundException: {}", errorresponse.getMessage());
        return ResponseEntity.status(e.getTaskErrorCode().getHttpStatus()).body(errorresponse);
    }

    // 로그인 - 사용자가 존재하지 않을 때
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException e) {
        ErrorResponse errorresponse = new ErrorResponse(e.appUserErrorCode.name(), e.appUserErrorCode.getHttpStatus(), e.getMessage());
        logger.error("UserNotFoundException: {}", errorresponse.getMessage());
        return ResponseEntity.status(e.appUserErrorCode.getHttpStatus()).body(errorresponse);
    }

    // 로그인 - 비밀번호 불일치
    @ExceptionHandler(PasswordNotMatchException.class)
    public ResponseEntity<ErrorResponse> handlePasswordNotMatchException(PasswordNotMatchException e) {
        ErrorResponse errorresponse = new ErrorResponse(e.appUserErrorCode.name(), e.appUserErrorCode.getHttpStatus(), e.getMessage());
        logger.error("PasswordNotMatchException: {}", errorresponse.getMessage());
        return ResponseEntity.status(e.appUserErrorCode.getHttpStatus()).body(errorresponse);
    }

    // 로그아웃 - 토큰이 유효하지 않을 때
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTokenException(InvalidTokenException e) {
        ErrorResponse errorresponse = new ErrorResponse(e.appUserErrorCode.name(), e.appUserErrorCode.getHttpStatus(), e.getMessage());
        logger.error("InvalidTokenException: {}", errorresponse.getMessage());
        return ResponseEntity.status(e.appUserErrorCode.getHttpStatus()).body(errorresponse);
    }
}
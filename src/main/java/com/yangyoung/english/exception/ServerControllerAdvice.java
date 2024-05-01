package com.yangyoung.english.exception;

import com.yangyoung.english.student.exception.StudentIdDuplicateException;
import com.yangyoung.english.student.exception.StudentNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice("com.yangyoung.server")
public class ServerControllerAdvice {

    private Logger logger = LoggerFactory.getLogger(ServerControllerAdvice.class);

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String exception(Exception exception) {
        logger.error("Internal Server Exception: {}", exception.getMessage());
        return exception.getMessage();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String illegalArgumentException(IllegalArgumentException exception) {
        logger.error("IllegalArgumentException: {}", exception.getMessage());
        return exception.getMessage();
    }

    @ExceptionHandler(CommonException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String notFoundException(CommonException exception) {
        logger.error("CommonException: {}", exception.getMessage());
        return exception.getMessage();
    }

    @ExceptionHandler(StudentIdDuplicateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String studentIdDuplicateException(StudentIdDuplicateException exception) {
        logger.error("StudentIdDuplicateException: {}", exception.getMessage());
        return exception.getMessage();
    }

    @ExceptionHandler(StudentNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String studentNotFoundException(StudentNotFoundException exception) {
        logger.error("StudentNotFoundException: {}", exception.getMessage());
        return exception.getMessage();
    }
}

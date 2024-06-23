package com.yangyoung.english.lecture.controller;

import com.yangyoung.english.lecture.dto.request.AddLectureByFormRequest;
import com.yangyoung.english.lecture.dto.request.LectureStudentAddRequest;
import com.yangyoung.english.lecture.dto.request.LectureStudentUpdateRequest;
import com.yangyoung.english.lecture.dto.request.LectureUpdateRequest;
import com.yangyoung.english.lecture.dto.response.LectureBriefResponse;
import com.yangyoung.english.lecture.dto.response.LectureResponse;
import com.yangyoung.english.lecture.service.LectureService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/lecture")
public class LectureController {

    private final LectureService lectureService;

    // 강의 정보 등록 - 폼 입력으로 등록 컨트롤러
    @PostMapping("")
    @Operation(summary = "강의 정보 등록 - 폼", description = "강의 정보를 등록합니다.")
    public ResponseEntity<LectureResponse> addLectureByForm(@RequestBody final AddLectureByFormRequest request) {
        return ResponseEntity.ok(lectureService.addLectureByForm(request));
    }

    // 강의 정보 등록 - 스프레드시트 읽기 컨트롤러
    @PostMapping("/sheet")
    @Operation(summary = "강의 정보 등록 - 스프레드시트", description = "스프레드시트를 읽어 강의 정보를 등록합니다.")
    public ResponseEntity<Void> addLecturesBySheet() throws Exception {
        lectureService.addLectureBySheet();
        return ResponseEntity.ok().build();
    }

    // 강의 학생 추가 컨트롤러
    @PostMapping("/student")
    @Operation(summary = "강의 학생 추가", description = "강의 학생을 추가합니다.")
    public ResponseEntity<LectureResponse> addLectureStudent(@RequestBody final LectureStudentAddRequest request) {

        lectureService.addStudentToLecture(request);
        return ResponseEntity.ok().build();
    }

    // 강의 전체 조회 - 페이징 처리 조회 컨트롤러
    @GetMapping("")
    @Operation(summary = "강의 전체 조회 - 페이징 처리", description = "강의 정보를 전체 조회합니다.")
    public ResponseEntity<Page<LectureResponse>> getLectures(@RequestParam(value = "page", defaultValue = "0") final int page,
                                                             @RequestParam(value = "size", defaultValue = "10") final int size) {
        return ResponseEntity.ok(lectureService.getAllLecture(page, size));
    }

    // 강의 전체 조회 - 달 단위
    @GetMapping("/month")
    @Operation(summary = "강의 전체 조회 - 달 단위", description = "강의 정보를 달 단위로 조회합니다.")
    public ResponseEntity<List<LectureResponse>> getLecturesByMonth(@RequestParam final int year, @RequestParam final int month) {
        return ResponseEntity.ok(lectureService.getAllLectureByMonth(year, month));
    }

    // 강의 전체 조회 - 주 단위
    @GetMapping("/week")
    @Operation(summary = "강의 전체 조회 - 주 단위", description = "강의 정보를 주 단위로 조회합니다.")
    public ResponseEntity<List<LectureResponse>> getLecturesByWeek(@RequestParam final LocalDate date) {
        return ResponseEntity.ok(lectureService.getAllLectureByWeek(date));
    }

    // 강의 전체 조회 - 날짜 단위
    @GetMapping("/day")
    @Operation(summary = "강의 전체 조회 - 날짜 단위", description = "강의 정보를 날짜 단위로 조회합니다.")
    public ResponseEntity<List<LectureResponse>> getLecturesByDate(@RequestParam final LocalDate date) {
        return ResponseEntity.ok(lectureService.getAllLectureByDate(date));
    }

    // 강의 상세 조회 컨트롤러
    @GetMapping("/{lectureId}")
    @Operation(summary = "강의 상세 조회", description = "강의 정보를 상세 조회합니다.")
    public ResponseEntity<LectureResponse> getLecture(@PathVariable final Long lectureId) {
        return ResponseEntity.ok(lectureService.getLecture(lectureId));
    }

    // 강의 정보 수정 컨트롤러
    @PatchMapping("")
    @Operation(summary = "강의 정보 수정", description = "강의 정보를 수정합니다.")
    public ResponseEntity<LectureResponse> updateLecture(@RequestBody final LectureUpdateRequest request) {
        return ResponseEntity.ok(lectureService.updateLecture(request));
    }

    // 강의 수강 학생 수정 컨트롤러
    @PatchMapping("/student")
    @Operation(summary = "강의 수강 학생 수정", description = "강의 수강 학생을 수정합니다.")
    public ResponseEntity<LectureResponse> updateLectureStudent(@RequestBody final LectureStudentUpdateRequest request) {
        return ResponseEntity.ok(lectureService.updateLectureStudents(request));
    }

    // 강의 정보 삭제 - single 컨트롤러
    @DeleteMapping("/{lectureId}")
    @Operation(summary = "강의 정보 삭제 - single", description = "강의 정보를 삭제합니다.")
    public ResponseEntity<Void> deleteLecture(@PathVariable final Long lectureId) {
        lectureService.deleteLecture(lectureId);
        return ResponseEntity.ok().build();
    }

    // 강의 정보 삭제 - multiple 컨트롤러
    @DeleteMapping("")
    @Operation(summary = "강의 정보 삭제 - multiple", description = "강의 정보를 삭제합니다.")
    public ResponseEntity<Void> deleteLectures(@RequestParam final List<Long> lectureIds) {
        lectureService.deleteLectures(lectureIds);
        return ResponseEntity.ok().build();
    }

    // 특정 학생이 수강하는 강의 조회 컨트롤러
    @GetMapping("/student/{studentId}")
    @Operation(summary = "특정 학생이 수강하는 강의 조회", description = "특정 학생이 수강하는 강의 목록을 조회합니다.")
    public ResponseEntity<List<LectureBriefResponse>> getLectureByStudent(@PathVariable(value = "studentId") final Long studentId) {

        List<LectureBriefResponse> responses = lectureService.getLecturesByStudent(studentId);

        return ResponseEntity.ok(responses);
    }
}

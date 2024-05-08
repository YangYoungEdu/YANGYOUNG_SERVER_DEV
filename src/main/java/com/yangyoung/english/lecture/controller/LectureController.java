package com.yangyoung.english.lecture.controller;

import com.yangyoung.english.lecture.dto.request.AddLectureByFormRequest;
import com.yangyoung.english.lecture.dto.request.LectureStudentUpdateRequest;
import com.yangyoung.english.lecture.dto.request.LectureUpdateRequest;
import com.yangyoung.english.lecture.dto.response.LectureResponse;
import com.yangyoung.english.lecture.service.LectureService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/lecture")
public class LectureController {

    private final LectureService lectureService;

    // 강의 정보 등록 - 폼 입력으로 등록 컨트롤러
    @PostMapping("")
    @Operation(summary = "강의 정보 등록 - 폼", description = "강의 정보를 등록합니다.")
    public ResponseEntity<LectureResponse> addLectureByForm(@RequestBody AddLectureByFormRequest request) {
        return ResponseEntity.ok(lectureService.addLectureByForm(request));
    }

    // 강의 전체 조회 - 페이징 처리 조회 컨트롤러
    @GetMapping("")
    @Operation(summary = "강의 전체 조회 - 페이징 처리", description = "강의 정보를 전체 조회합니다.")
    public ResponseEntity<Page<LectureResponse>> getLectures(@RequestParam(value = "page", defaultValue = "0") int page,
                                                             @RequestParam(value = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(lectureService.getAllLecture(page, size));
    }

    // 강의 상세 조회 컨트롤러
    @GetMapping("/{lectureId}")
    @Operation(summary = "강의 상세 조회", description = "강의 정보를 상세 조회합니다.")
    public ResponseEntity<LectureResponse> getLecture(@PathVariable Long lectureId) {
        return ResponseEntity.ok(lectureService.getLecture(lectureId));
    }

    // 강의 정보 수정 컨트롤러
    @PatchMapping("")
    @Operation(summary = "강의 정보 수정", description = "강의 정보를 수정합니다.")
    public ResponseEntity<LectureResponse> updateLecture(@RequestBody LectureUpdateRequest request) {
        return ResponseEntity.ok(lectureService.updateLecture(request));
    }

    // 강의 수강 학생 수정 컨트롤러
    @PatchMapping("/student")
    @Operation(summary = "강의 수강 학생 수정", description = "강의 수강 학생을 수정합니다.")
    public ResponseEntity<LectureResponse> updateLectureStudent(@RequestBody LectureStudentUpdateRequest request) {
        return ResponseEntity.ok(lectureService.updateLectureStudents(request));
    }

    // 강의 정보 삭제 - single 컨트롤러
    @DeleteMapping("/{lectureId}")
    @Operation(summary = "강의 정보 삭제 - single", description = "강의 정보를 삭제합니다.")
    public ResponseEntity<Void> deleteLecture(@PathVariable Long lectureId) {
        lectureService.deleteLecture(lectureId);
        return ResponseEntity.ok().build();
    }

    // 강의 정보 삭제 - multiple 컨트롤러
    @DeleteMapping("")
    @Operation(summary = "강의 정보 삭제 - multiple", description = "강의 정보를 삭제합니다.")
    public ResponseEntity<Void> deleteLectures(@RequestParam List<Long> lectureIds) {
        lectureService.deleteLectures(lectureIds);
        return ResponseEntity.ok().build();
    }
}

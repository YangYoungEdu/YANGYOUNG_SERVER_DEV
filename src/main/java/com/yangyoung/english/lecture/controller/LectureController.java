package com.yangyoung.english.lecture.controller;

import com.yangyoung.english.lecture.dto.request.*;
import com.yangyoung.english.lecture.dto.response.LectureBriefResponse;
import com.yangyoung.english.lecture.dto.response.LectureResponse;
import com.yangyoung.english.lecture.service.LectureService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public ResponseEntity<LectureResponse> addLectureByForm(@RequestBody final AddLectureByFormRequest request,
                                                            @RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok(lectureService.addLectureByForm(request));
    }

    // 강의 정보 등록 - 스프레드시트 읽기 컨트롤러
    @PostMapping("/sheet")
    @Operation(summary = "강의 정보 등록 - 스프레드시트", description = "스프레드시트를 읽어 강의 정보를 등록합니다.")
    public ResponseEntity<Void> addLecturesBySheet(@RequestHeader(value = "Authorization") String token) throws Exception {
        lectureService.addLectureBySheet();
        return ResponseEntity.ok().build();
    }

    // 강의 학생 추가 컨트롤러
    @PostMapping("/student")
    @Operation(summary = "강의 학생 추가", description = "강의 학생을 추가합니다.")
    public ResponseEntity<LectureResponse> addLectureStudent(@RequestBody final LectureStudentAddRequest request,
                                                             @RequestHeader(value = "Authorization") String token) {

        lectureService.addStudentToLecture(request);
        return ResponseEntity.ok().build();
    }

    // 강의 전체 조회 - 달 단위
    @GetMapping("/month")
    @Operation(summary = "강의 전체 조회 - 달 단위", description = "강의 정보를 달 단위로 조회합니다.")
    public ResponseEntity<List<LectureResponse>> getLecturesByMonth(@RequestParam final int year,
                                                                    @RequestParam final int month,
                                                                    @RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok(lectureService.getAllLectureByMonth(year, month));
    }

    // 강의 전체 조회 - 주 단위
    @GetMapping("/week")
    @Operation(summary = "강의 전체 조회 - 주 단위", description = "강의 정보를 주 단위로 조회합니다.")
    public ResponseEntity<List<LectureResponse>> getLecturesByWeek(@RequestParam final LocalDate date,
                                                                   @RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok(lectureService.getAllLectureByWeek(date));
    }

    // 강의 전체 조회 - 날짜 단위
    @GetMapping("/day")
    @Operation(summary = "강의 전체 조회 - 날짜 단위", description = "강의 정보를 날짜 단위로 조회합니다.")
    public ResponseEntity<List<LectureResponse>> getLecturesByDate(@RequestParam final LocalDate date,
                                                                   @RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok(lectureService.getAllLectureByDate(date));
    }

    // 강의 상세 조회 컨트롤러
    @GetMapping("/{lectureId}")
    @Operation(summary = "강의 상세 조회", description = "강의 정보를 상세 조회합니다.")
    public ResponseEntity<LectureResponse> getLecture(@PathVariable final Long lectureId,
                                                      @RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok(lectureService.getLecture(lectureId));
    }

    // 강의 정보 수정 컨트롤러
    @PatchMapping("")
    @Operation(summary = "강의 정보 수정", description = "강의 정보를 수정합니다.")
    public ResponseEntity<LectureResponse> updateLecture(@RequestBody final LectureUpdateRequest request,
                                                         @RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok(lectureService.updateLecture(request));
    }

    // 강의 수업 날짜 수정 컨트롤러
    @PatchMapping("/date")
    @Operation(summary = "강의 수업 날짜 수정", description = "강의 수업 날짜를 수정합니다.")
    public ResponseEntity<LectureResponse> updateLectureDate(@RequestBody final LectureDateUpdateRequest request,
                                                             @RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok(lectureService.updateLectureDate(request));
    }

    // 강의 수강 학생 수정 컨트롤러
    @PatchMapping("/student")
    @Operation(summary = "강의 수강 학생 수정", description = "강의 수강 학생을 수정합니다.")
    public ResponseEntity<LectureResponse> updateLectureStudent(@RequestBody final LectureStudentUpdateRequest request,
                                                                @RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok(lectureService.updateLectureStudents(request));
    }

    // 강의 정보 삭제 - multiple 컨트롤러
    @DeleteMapping("")
    @Operation(summary = "강의 정보 삭제 - multiple", description = "강의 정보를 삭제합니다.")
    public ResponseEntity<Void> deleteLectures(@RequestParam final List<Long> lectureIds,
                                               @RequestHeader(value = "Authorization") String token) {
        lectureService.deleteLectures(lectureIds);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{lectureId}")
    @Operation(summary = "강의 정보 삭제 - single", description = "강의 정보를 삭제합니다.")
    public ResponseEntity<Void> deleteLecture(@PathVariable final Long lectureId,
                                              @RequestParam final boolean isRepeated,
                                              @RequestHeader(value = "Authorization") String token) {
        lectureService.deleteLecture(lectureId, isRepeated);
        return ResponseEntity.ok().build();
    }

    // 특정 학생이 수강하는 강의 조회 컨트롤러
    @GetMapping("/student/{studentId}")
    @Operation(summary = "특정 학생이 수강하는 강의 조회", description = "특정 학생이 수강하는 강의 목록을 조회합니다.")
    public ResponseEntity<List<LectureBriefResponse>> getLectureByStudent(@PathVariable(value = "studentId") final Long studentId,
                                                                          @RequestHeader(value = "Authorization") String token) {

        List<LectureBriefResponse> responses = lectureService.getLecturesByStudent(studentId);

        return ResponseEntity.ok(responses);
    }

    // 강의 복사
    @PostMapping("/copy")
    @Operation(summary = "강의 복사", description = "강의를 복사합니다.")
    public ResponseEntity<LectureResponse> copyLecture(@RequestParam final Long lectureId,
                                                       @RequestParam final LocalDate newLectureDate,
                                                       @RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok(lectureService.copyLecture(lectureId, newLectureDate));
    }

    // 강의 수정 - 드래그 앤 드롭
    @PatchMapping("/drag")
    @Operation(summary = "강의 수정 - 드래그 앤 드롭", description = "강의를 드래그 앤 드롭하여 수정합니다.")
    public ResponseEntity<LectureResponse> dragLecture(@RequestBody final LectureUpdateRequestByDAD request,
                                                       @RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok(lectureService.updateLectureByDAD(request));
    }
}

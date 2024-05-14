package com.yangyoung.english.student.dto.response;

import com.yangyoung.english.lecture.dto.response.LectureBriefResponse;
import com.yangyoung.english.lecture.dto.response.LectureResponse;
import com.yangyoung.english.task.dto.response.StudentTaskResponse;
import com.yangyoung.english.task.dto.response.TaskBriefResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StudentScheduleResponse {

    private StudentBriefResponse student;

    private List<LectureBriefResponse> lectureList;

    private List<TaskBriefResponse> taskList;
}

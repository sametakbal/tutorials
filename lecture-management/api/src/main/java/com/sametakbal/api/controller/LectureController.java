package com.sametakbal.api.controller;

import com.sametakbal.api.entity.Lecture;
import com.sametakbal.api.service.ILectureService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/lectures")
public class LectureController {

    private final ILectureService lectureService;

    public LectureController(ILectureService lectureService) {
        this.lectureService = lectureService;
    }

    @GetMapping
    public ResponseEntity<List<Lecture>> getLectures(){
        return ResponseEntity.ok(lectureService.getAll());
    }
}

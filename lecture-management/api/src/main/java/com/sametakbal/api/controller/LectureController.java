package com.sametakbal.api.controller;

import com.sametakbal.api.entity.Lecture;
import com.sametakbal.api.service.ILectureService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lectures")
public class LectureController {

    private final ILectureService lectureService;

    public LectureController(ILectureService lectureService) {
        this.lectureService = lectureService;
    }

    @GetMapping
    public ResponseEntity<Page<Lecture>> getLectures(@RequestParam(defaultValue = "0") Integer page,
                                                     @RequestParam(defaultValue = "10") Integer pageSize ){
        return ResponseEntity.ok(lectureService.getAll(PageRequest.of(page,pageSize, Sort.by("id"))));
    }

    @PostMapping
    public ResponseEntity<Lecture> createLecture(@RequestBody Lecture lecture){
        return ResponseEntity.ok(lectureService.save(lecture));
    }
    @GetMapping("/{id}")
    public ResponseEntity<Lecture> getLectureById(@PathVariable Integer id){
        return ResponseEntity.ok(lectureService.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Lecture> deleteLectureById(@PathVariable Integer id){
        lectureService.delete(id);
        return ResponseEntity.ok().build();
    }
}

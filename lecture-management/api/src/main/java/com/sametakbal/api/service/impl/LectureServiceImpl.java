package com.sametakbal.api.service.impl;

import com.sametakbal.api.entity.Lecture;
import com.sametakbal.api.repository.ILectureRepository;
import com.sametakbal.api.service.ILectureService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LectureServiceImpl implements ILectureService {

    private final ILectureRepository lectureRepository;

    public LectureServiceImpl(ILectureRepository lectureRepository) {
        this.lectureRepository = lectureRepository;
    }

    @Override
    public Lecture save(Lecture dto) {
        return null;
    }

    @Override
    public Lecture getById(Integer id) {
        return null;
    }

    @Override
    public List<Lecture> getAll() {
        return lectureRepository.findAll();
    }
}

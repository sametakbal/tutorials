package com.sametakbal.api.service;

import java.util.List;

public interface IService<T> {
    T save(T t);
    T getById(Integer id);
    List<T> getAll();
}

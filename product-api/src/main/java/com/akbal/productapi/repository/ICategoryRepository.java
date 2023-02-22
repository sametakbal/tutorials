package com.akbal.productapi.repository;

import com.akbal.productapi.entity.Category;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICategoryRepository extends CrudRepository<Category,Integer> {
}

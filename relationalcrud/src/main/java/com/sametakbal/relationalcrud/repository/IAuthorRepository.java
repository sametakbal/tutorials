package com.sametakbal.relationalcrud.repository;

import com.sametakbal.relationalcrud.entity.Author;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAuthorRepository extends CrudRepository<Author,Integer> {
}

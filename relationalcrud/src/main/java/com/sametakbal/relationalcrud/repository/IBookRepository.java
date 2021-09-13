package com.sametakbal.relationalcrud.repository;

import com.sametakbal.relationalcrud.entity.Book;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IBookRepository extends CrudRepository<Book,Integer> {
}

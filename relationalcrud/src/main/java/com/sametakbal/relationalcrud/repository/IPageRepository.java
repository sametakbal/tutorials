package com.sametakbal.relationalcrud.repository;

import com.sametakbal.relationalcrud.entity.Page;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPageRepository extends CrudRepository<Page,Integer> {
}

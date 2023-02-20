package com.akbal.productapi.repository;

import com.akbal.productapi.entity.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IProductRepository extends CrudRepository<Product,Integer> {
    List<Product> findAllByCategoryId(Integer categoryId);
}

package com.akbal.productapi.controller;

import com.akbal.productapi.common.ServiceResult;
import com.akbal.productapi.entity.Category;
import com.akbal.productapi.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<ServiceResult<List<Category>>> fetchAll(){
        return new ResponseEntity<>(categoryService.getAllCategories(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ServiceResult<Void>> createCategory(@RequestBody Category category){
        return new ResponseEntity<>(categoryService.saveCategory(category), HttpStatus.OK);
    }


    @PutMapping
    public ResponseEntity<ServiceResult<Void>> updateCategory(@RequestBody Category category){
        return new ResponseEntity<>(categoryService.saveCategory(category), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ServiceResult<Void>> deleteCategory(@PathVariable Integer id){
        return new ResponseEntity<>(categoryService.deleteCategory(id),HttpStatus.OK);
    }
}

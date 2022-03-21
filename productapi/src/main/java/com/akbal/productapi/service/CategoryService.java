package com.akbal.productapi.service;

import com.akbal.productapi.common.ServiceResult;
import com.akbal.productapi.entity.Category;
import com.akbal.productapi.entity.Product;
import com.akbal.productapi.repository.ICategoryRepository;
import com.akbal.productapi.repository.IProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    private final ICategoryRepository categoryRepository;
    private final IProductRepository productRepository;

    public CategoryService(ICategoryRepository categoryRepository, IProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    public ServiceResult<Void> saveCategory(Category category){
        ServiceResult<Void> result = new ServiceResult<>();
        if (category.getName() == null) {
            result.setSuccess(false);
            result.setErrorMessage("Name can not be null!");
        }
        categoryRepository.save(category);
        return result;
    }

    public ServiceResult<List<Category>> getAllCategories(){
        return new ServiceResult<>((List<Category>) categoryRepository.findAll());
    }

    public  ServiceResult<Void> deleteCategory(Integer id){
        ServiceResult<Void> result = new ServiceResult<>();
        Optional<Category> categoryOptional = categoryRepository.findById(id);

        if (categoryOptional.isEmpty()){
            result.setSuccess(false);
            result.setErrorMessage("Category not found!");
            return result;
        }

        List<Product> productsByCategory = productRepository.findAllByCategoryId(id);

        if (!productsByCategory.isEmpty()) {
            result.setSuccess(false);
            result.setErrorMessage("This category can not delete!");
            return result;
        }

        categoryRepository.delete(categoryOptional.get());
        return result;
    }

}

package com.akbal.productapi.service;

import com.akbal.productapi.common.ServiceResult;
import com.akbal.productapi.entity.Product;
import com.akbal.productapi.repository.IProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final IProductRepository productRepository;

    public ProductService(IProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ServiceResult<Void> saveProduct(Product product){
        ServiceResult<Void> result = new ServiceResult<>();
        if (product.getTitle() == null || product.getPrice() == null || product.getCategory() == null) {
            result.setSuccess(false);
            result.setErrorMessage("Please fill all fields");
            return result;
        }
        productRepository.save(product);
        return result;
    }

    public ServiceResult<List<Product>> getAllProducts(){
        return new ServiceResult<>((List<Product>) productRepository.findAll());
    }

    public  ServiceResult<Void> deleteProduct(Integer id){
        ServiceResult<Void> result = new ServiceResult<>();
        Optional<Product> productOptional = productRepository.findById(id);

        if (productOptional.isEmpty()){
            result.setSuccess(false);
            result.setErrorMessage("Product not found!");
            return result;
        }
        productRepository.delete(productOptional.get());
        return result;
    }

}

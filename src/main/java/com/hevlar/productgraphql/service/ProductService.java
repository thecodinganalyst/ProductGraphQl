package com.hevlar.productgraphql.service;

import com.hevlar.productgraphql.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    public ProductService(ProductRepository productRepository, CategoryService categoryService){
        this.productRepository = productRepository;
        this.categoryService = categoryService;
    }

//    public List<Product> getProductsByCategory(List<String> categoryHierarchy){
//        categoryService.getCategory(categoryHierarchy)
//                .map(category -> productRepository.find)
//
//    }

}

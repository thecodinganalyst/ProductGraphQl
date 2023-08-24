package com.hevlar.productgraphql.service;

import com.hevlar.productgraphql.model.Product;
import com.hevlar.productgraphql.repository.ProductRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    public ProductService(ProductRepository productRepository, CategoryService categoryService){
        this.productRepository = productRepository;
        this.categoryService = categoryService;
    }

    public Flux<Product> getProductsByCategory(List<String> categoryHierarchy){
        return categoryService.getCategory(categoryHierarchy)
                .flatMapMany(productRepository::findAllByCategory);
    }

}

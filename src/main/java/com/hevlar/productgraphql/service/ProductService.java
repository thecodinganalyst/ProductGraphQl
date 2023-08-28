package com.hevlar.productgraphql.service;

import com.hevlar.productgraphql.model.Product;
import com.hevlar.productgraphql.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    public ProductService(ProductRepository productRepository, CategoryService categoryService){
        this.productRepository = productRepository;
        this.categoryService = categoryService;
    }

    public Flux<Product> getProductsByCategoryHierarchy(List<String> categoryHierarchy){
        if(CollectionUtils.isEmpty(categoryHierarchy))
            return Flux.error(new IllegalArgumentException("Category hierarchy is empty"));
        return categoryService.validateCategoryHierarchy(categoryHierarchy)
                .flatMapMany(found -> productRepository.findAllByCategory(categoryHierarchy));
    }

    public Mono<Product> addProduct(Product product){
        if(product == null) return Mono.error(new IllegalArgumentException("Product is null"));
        if(CollectionUtils.isEmpty(product.getCategory())) return Mono.error(new IllegalArgumentException("Product category is null"));

        return categoryService.validateCategoryHierarchy(product.getCategory())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Category not found")))
                .flatMap(category -> productRepository.save(product));
    }

}

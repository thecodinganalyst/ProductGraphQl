package com.hevlar.productgraphql.service;

import com.hevlar.productgraphql.model.Product;
import com.hevlar.productgraphql.model.Variant;
import com.hevlar.productgraphql.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
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

    public Mono<Product> getProduct(String productId){
        return productRepository.findById(productId);
    }

    public Mono<Product> addVariant(String productId, Variant variant){
        if(productId == null) return Mono.error(new IllegalArgumentException("ProductId is null"));
        if(variant == null) return Mono.error(new IllegalArgumentException("Variant is null"));

        return getProduct(productId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Product not found")))
                .flatMap(product -> {
                    if(product.hasVariantOfName(variant.name()))
                        return Mono.error(new IllegalArgumentException("Variant name already exists"));
                    List<Variant> updatedVariantList = new ArrayList<>(product.getVariants());
                    updatedVariantList.add(variant);
                    product.setVariants(updatedVariantList);
                    return productRepository.save(product);
                });
    }

}

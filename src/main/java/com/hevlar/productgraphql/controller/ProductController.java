package com.hevlar.productgraphql.controller;

import com.hevlar.productgraphql.model.Product;
import com.hevlar.productgraphql.service.ProductService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Controller
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService){
        this.productService = productService;
    }

    @QueryMapping
    public Flux<Product> getProductsByCategory(@Argument List<String> categoryHierarchy){
        return productService.getProductsByCategoryHierarchy(categoryHierarchy);
    }

    @MutationMapping
    public Mono<Product> addProduct(@Argument Product newProduct){
        return productService.addProduct(newProduct);
    }
}

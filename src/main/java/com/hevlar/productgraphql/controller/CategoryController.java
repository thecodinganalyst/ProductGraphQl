package com.hevlar.productgraphql.controller;

import com.hevlar.productgraphql.model.Category;
import com.hevlar.productgraphql.service.CategoryService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Controller
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService){
        this.categoryService = categoryService;
    }

    @QueryMapping
    public Flux<Category> getCategories(){
        return categoryService.getCategories();
    }

    @QueryMapping
    public Mono<Category> getCategory(@Argument List<String> categoryHierarchy){
        return categoryService.getCategory(categoryHierarchy);
    }
    @MutationMapping
    public Mono<Category> addTopCategory(@Argument Category category){
        return categoryService.addTopCategory(category);
    }

    @MutationMapping
    public Mono<Category> addCategoryToExisting(@Argument Category newCategory, @Argument List<String> existingCategory){
        return categoryService.addCategoryToExisting(newCategory, existingCategory);
    }
}

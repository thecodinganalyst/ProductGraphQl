package com.hevlar.productgraphql.service;

import com.hevlar.productgraphql.model.Category;
import com.hevlar.productgraphql.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
    }

    public Flux<Category> getCategories(){
        return categoryRepository.findAll();
    }

    public Mono<Category> addTopCategory(Category category){
        Category newCategory = Category.deepCopyCategory(category);
        return categoryRepository.save(newCategory);
    }

    public Mono<Category> getCategory(List<String> categoryHierarchy){
        if(categoryHierarchy == null || categoryHierarchy.size() == 0)
            throw new IllegalArgumentException("Category hierarchy is missing");

        return categoryRepository.findByName(categoryHierarchy.get(0))
                .map(category ->
                    findTargetCategory(category, categoryHierarchy.subList(1, categoryHierarchy.size()))
                );
    }

    public Mono<Category> addCategoryToExisting(Category newCategory, List<String> existingCategory){
        if(newCategory == null) throw new IllegalArgumentException("New category is null");
        if(existingCategory == null || existingCategory.size() == 0) addTopCategory(newCategory);

        return categoryRepository.findByName(existingCategory.get(0))
                .map(category -> {
                    Category target = findTargetCategory(category, existingCategory.subList(1, existingCategory.size()));
                    target.subCategories().add(Category.deepCopyCategory(newCategory));
                    categoryRepository.save(category);
                    return category;
                });
    }

    private Category findTargetCategory(Category currentCategory, List<String> subCategoryNames){
        for(String categoryName: subCategoryNames){
            currentCategory = currentCategory.subCategories()
                    .stream()
                    .filter(category -> category.name().equals(categoryName))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Existing category provided doesn't exist"));
        }
        return currentCategory;
    }

}

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
        if (category == null || category.name() == null) {
            return Mono.error(new IllegalArgumentException("Invalid category"));
        }
        return categoryRepository.findByName(category.name())
                .flatMap(existingCategory -> Mono.<Category>error(new IllegalArgumentException("A category with this name already exists")))
                .switchIfEmpty(Mono.defer(() -> {
                    Category newCategory = Category.deepCopyCategory(category);
                    return categoryRepository.save(newCategory);
                }));

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
        if(existingCategory == null || existingCategory.size() == 0) return addTopCategory(newCategory);

        return categoryRepository.findByName(existingCategory.get(0))
                .switchIfEmpty(Mono.error(() -> new IllegalArgumentException("Existing category not found")))
                .flatMap(category ->
                    Mono.defer(() -> {
                        Category target = findTargetCategory(category, existingCategory.subList(1, existingCategory.size()));
                        if(target.subCategories()
                                .stream()
                                .map(Category::name)
                                .anyMatch(name -> name.equals(newCategory.name()))){
                            throw new IllegalArgumentException("New category already exists");
                        }
                        target.subCategories().add(Category.deepCopyCategory(newCategory));
                        return categoryRepository.save(category);
                    })
                );
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

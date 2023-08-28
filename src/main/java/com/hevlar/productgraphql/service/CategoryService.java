package com.hevlar.productgraphql.service;

import com.hevlar.productgraphql.model.Category;
import com.hevlar.productgraphql.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
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
        if (category == null || category.getName() == null || category.getName().trim().length() == 0) {
            return Mono.error(new IllegalArgumentException("Invalid category"));
        }
        return categoryRepository.findByName(category.getName())
                .flatMap(existingCategory -> Mono.<Category>error(new IllegalArgumentException("A category with this name already exists")))
                .switchIfEmpty(Mono.defer(() -> categoryRepository.save(category)));
    }

    public Mono<Category> getTopCategory(String categoryName){
        if(categoryName == null || categoryName.trim().length() == 0)
            return Mono.error(new IllegalArgumentException("Category name is missing"));

        return categoryRepository.findByName(categoryName);
    }

    public Mono<Category> validateCategoryHierarchy(List<String> categoryHierarchy){
        if(CollectionUtils.isEmpty(categoryHierarchy))
            return Mono.error(new IllegalArgumentException("Category hierarchy is missing"));

        return getTopCategory(categoryHierarchy.get(0))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Category not found")))
                .map(category -> {
                    if(categoryHierarchy.size() > 1){
                        return category.traverse(categoryHierarchy.subList(1, categoryHierarchy.size()));
                    }else{
                        return category;
                    }
                });
    }

    public Mono<Category> addCategoryToExisting(Category newCategory, List<String> existingCategory){
        if(newCategory == null) return Mono.error(new IllegalArgumentException("New category is null"));
        if(existingCategory == null || existingCategory.size() == 0) return addTopCategory(newCategory);

        return categoryRepository.findByName(existingCategory.get(0))
                .switchIfEmpty(Mono.error(() -> new IllegalArgumentException("Existing category not found")))
                .flatMap(category -> {
                    Category target = category.traverse(existingCategory.subList(1, existingCategory.size()));
                    if(target.hasSubCategory(newCategory.getName())){
                        return Mono.error(new IllegalArgumentException("New category already exists"));
                    }
                    List<Category> updatedSubCategoryList = new ArrayList<>(target.getSubCategories());
                    updatedSubCategoryList.add(newCategory);
                    target.setSubCategories(updatedSubCategoryList);
                    return categoryRepository.save(category);
                });
    }



}

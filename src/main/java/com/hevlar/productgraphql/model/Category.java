package com.hevlar.productgraphql.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@Data
public class Category{
    @Id
    String id;
    String name;
    List<Category> subCategories;

    public Category(String id, String name, List<Category> subCategories){
        this.id = id;
        this.name = name;
        this.subCategories = subCategories != null ? subCategories : List.of();
    }

    public boolean hasSubCategoryOfName(String name){
        for (Category sub: subCategories) {
            if(sub.name.equals(name)){
                return true;
            }
        }
        return false;
    }

    public Category traverse(List<String> subCategoryNames){
        if(subCategoryNames == null || subCategoryNames.size() == 0) return this;
        for(Category subCategory: subCategories){
            if(subCategory.getName().equals(subCategoryNames.get(0))){
                if(subCategoryNames.size() > 1){
                    return traverse(subCategory, subCategoryNames.subList(1, subCategoryNames.size()));
                }else {
                    return subCategory;
                }
            }
        }
        throw new IllegalArgumentException("Existing category provided doesn't exist");
    }

    private Category traverse(Category category, List<String> subCategoryNames){
        for(Category subCategory: category.subCategories){
            if(subCategory.getName().equals(subCategoryNames.get(0))){
                if(subCategoryNames.size() > 1){
                    return traverse(subCategory, subCategoryNames.subList(1, subCategoryNames.size()));
                }else {
                    return subCategory;
                }
            }
        }
        throw new IllegalArgumentException("Existing category provided doesn't exist");
    }
}

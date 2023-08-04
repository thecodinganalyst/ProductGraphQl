package com.hevlar.productgraphql.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document
public record Category(
        @Id
        String id,
        String name,
        List<Category> subCategories
) {

    // Utility method for deep copying a Category object
    public static Category deepCopyCategory(Category originalCategory) {
        if (originalCategory == null) {
            return null;
        } else {
            return new Category(
                    originalCategory.id(),
                    originalCategory.name(),
                    deepCopySubCategories(originalCategory.subCategories())
            );
        }
    }

    // Helper method to make a deep copy of the subCategories list
    private static List<Category> deepCopySubCategories(List<Category> originalList) {
        if (originalList == null) {
            return List.of();
        } else {
            List<Category> newList = new ArrayList<>();
            for (Category subCategory : originalList) {
                newList.add(deepCopyCategory(subCategory)); // Recursive call to copy subCategory
            }
            return newList;
        }
    }
}

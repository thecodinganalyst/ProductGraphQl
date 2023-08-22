package com.hevlar.productgraphql.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.is;

class CategoryTest {

    Category subSubCategory1 = new Category("4", "Sub SubCategory 1", List.of());
    Category subSubCategory2 = new Category("5", "Sub SubCategory 2", List.of());
    Category subCategory1 = new Category("1", "SubCategory 1", List.of(subSubCategory1, subSubCategory2));
    Category subCategory2 = new Category("2", "SubCategory 2", List.of());
    Category category = new Category("3", "Category", List.of(subCategory1, subCategory2));

    @Test
    void hasSubCategoryOfName() {
        assertThat(category.hasSubCategoryOfName("SubCategory 1"), is(true));
        assertThat(category.hasSubCategoryOfName("SubCategory 2"), is(true));
        assertThat(category.hasSubCategoryOfName("SubCategory 3"), is(false));
    }

    @Test
    void givenEmptyList_whenTraverse_thenGetResultCorrectly(){
        Category result = category.traverse(List.of());
        assertThat(result.getName(), is("Category"));
        assertThat(result.getSubCategories().size(), is(2));
    }

    @Test
    void whenTraverse_thenGetResultCorrectly(){
        Category result = category.traverse(List.of("SubCategory 1"));
        assertThat(result.getName(), is("SubCategory 1"));
        assertThat(result.getSubCategories().size(), is(2));
    }

    @Test
    void whenTraverseMultipleLevels_thenGetResultCorrectly(){
        Category result = category.traverse(List.of("SubCategory 1", "Sub SubCategory 2"));
        assertThat(result.getName(), is("Sub SubCategory 2"));
        assertThat(result.getSubCategories().size(), is(0));
    }
}

package com.hevlar.productgraphql.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CategoryTest {

    Category sofa2Seater = new Category("6", "2 Seater Sofa", List.of());
    Category sofa = new Category("4", "Sofa", List.of(sofa2Seater));
    Category coffeeTable = new Category("5", "Coffee Table", List.of());
    Category livingRoom = new Category("1", "Living Room", List.of(sofa, coffeeTable));
    Category kitchen = new Category("2", "Kitchen", List.of());
    Category furniture = new Category("3", "Furniture", List.of(livingRoom, kitchen));

    @Test
    void hasSubCategoryOfName() {
        assertThat(furniture.hasSubCategoryOfName("Living Room"), is(true));
        assertThat(furniture.hasSubCategoryOfName("Kitchen"), is(true));
        assertThat(furniture.hasSubCategoryOfName("Bedroom"), is(false));
    }

    @Test
    void givenEmptyList_whenTraverse_thenGetResultCorrectly(){
        Category result = furniture.traverse(List.of());
        assertThat(result.getName(), is("Furniture"));
        assertThat(result.getSubCategories().size(), is(2));
    }

    @Test
    void given1Level_whenTraverse_thenGetResultCorrectly(){
        Category result = furniture.traverse(List.of("Living Room"));
        assertThat(result.getName(), is("Living Room"));
        assertThat(result.getSubCategories().size(), is(2));
    }

    @Test
    void given2Levels_whenTraverse_thenGetResultCorrectly(){
        Category result = furniture.traverse(List.of("Living Room", "Coffee Table"));
        assertThat(result.getName(), is("Coffee Table"));
        assertThat(result.getSubCategories().size(), is(0));
    }

    @Test
    void given3Levels_whenTraverse_thenGetResultCorrectly(){
        Category result = furniture.traverse(List.of("Living Room", "Sofa", "2 Seater Sofa"));
        assertThat(result.getName(), is("2 Seater Sofa"));
        assertThat(result.getSubCategories().size(), is(0));
    }

    @Test
    void givenSubCategoryNamesIsNull_whenTraverse_thenReturnCurrentCategory(){
        assertThat(furniture.traverse(null), is(furniture));
    }

    @Test
    void givenSubCategoryNamesIsEmpty_whenTraverse_thenReturnCurrentCategory(){
        assertThat(furniture.traverse(List.of()), is(furniture));
    }

    @Test
    void givenSubCategoryNameNotExist_whenTraverse_thenThrow(){
        assertThrows(
                IllegalArgumentException.class,
                () -> furniture.traverse(List.of("Something")),
                "Existing category provided doesn't exist"
        );
    }

    @Test
    void givenSubCategoryNameFrom2ndLevelNotExist_whenTraverse_thenThrow(){
        assertThrows(
                IllegalArgumentException.class,
                () -> furniture.traverse(List.of("Living Room", "Something")),
                "Existing category provided doesn't exist"
        );
    }
}

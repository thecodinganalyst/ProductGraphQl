package com.hevlar.productgraphql.service;

import com.hevlar.productgraphql.model.Category;
import com.hevlar.productgraphql.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    CategoryRepository categoryRepository;

    @InjectMocks
    CategoryService categoryService;

    @Test
    void whenGetTopCategory_shouldReturnCorrectly(){
        Category categoryFromRepo = new Category("Furniture", List.of());
        given(categoryRepository.findByName("Furniture")).willReturn(Mono.just(categoryFromRepo));

        StepVerifier.create(categoryService.getTopCategory("Furniture"))
                .expectNextMatches(category ->
                        category.getName().equals("Furniture") &&
                        category.getSubCategories().size() == 0
                )
                .verifyComplete();
    }

    @Test
    void givenCategoryNameIsNull_whenGetTopCategory_thenThrow(){
        StepVerifier.create(categoryService.getTopCategory(null))
                .expectErrorMessage("Category name is missing")
                .verify();
    }

    @Test
    void givenCategoryNameIsEmpty_whenGetTopCategory_thenThrow(){
        StepVerifier.create(categoryService.getTopCategory("  "))
                .expectErrorMessage("Category name is missing")
                .verify();
    }

    @Test
    void whenGetCategories_thenCallRepoFindAll(){
        Category cat1 = new Category("Cat 1", List.of());
        Category cat2 = new Category("Cat 2", List.of());
        given(categoryRepository.findAll()).willReturn(Flux.just(cat1, cat2));
        StepVerifier.create(categoryService.getCategories())
                .expectNext(cat1, cat2)
                .verifyComplete();
    }

    @Test
    void givenCategoryIsNull_whenAddTopCategory_thenThrow(){
        StepVerifier.create(categoryService.addTopCategory(null))
                .expectErrorMessage("Invalid category")
                .verify();
    }

    @Test
    void givenCategoryNameIsNull_whenAddTopCategory_thenThrow(){
        Category test = new Category(null, List.of());
        StepVerifier.create(categoryService.addTopCategory(test))
                .expectErrorMessage("Invalid category")
                .verify();
    }

    @Test
    void givenCategoryNameIsEmpty_whenAddTopCategory_thenThrow(){
        Category test = new Category("    ", List.of());
        StepVerifier.create(categoryService.addTopCategory(test))
                .expectErrorMessage("Invalid category")
                .verify();
    }

    @Test
    void givenTopCategoryNameExists_whenAddTopCategory_thenThrow(){
        Category testCategory = new Category("Test", List.of());
        given(categoryRepository.findByName("Test")).willReturn(Mono.just(testCategory));
        StepVerifier.create(categoryService.addTopCategory(testCategory))
                .expectErrorMessage("A category with this name already exists")
                .verify();
    }

    @Test
    void whenAddTopCategory_thenReturnCategory(){
        Category testCategory = new Category("Test", List.of());
        given(categoryRepository.findByName("Test")).willReturn(Mono.empty());
        given(categoryRepository.save(testCategory)).willReturn(Mono.just(testCategory));
        StepVerifier.create(categoryService.addTopCategory(testCategory))
                .expectNext(testCategory)
                .verifyComplete();
    }

    @Test
    void whenNewCategoryIsNull_whenAddCategoryToExisting_thenThrow(){
        Mono<Category> addCategoryToExisting = categoryService.addCategoryToExisting(
                null,
                List.of("Furniture"));
        StepVerifier.create(addCategoryToExisting)
                .expectErrorMessage("New category is null")
                .verify();
    }

    @Test
    void whenExistingCategoryIsNull_whenAddCategoryToExisting_thenCategoryIsAddedAsTopCategory(){
        Category testCategory = new Category("Test", List.of());
        given(categoryRepository.findByName("Test")).willReturn(Mono.empty());
        given(categoryRepository.save(any())).willReturn(Mono.just(testCategory));
        CategoryService spyCategoryService = spy(categoryService);

        Mono<Category> addCategoryToExisting = spyCategoryService.addCategoryToExisting(
                testCategory,
                null);
        StepVerifier.create(addCategoryToExisting)
                .expectNext(testCategory)
                .verifyComplete();
        verify(spyCategoryService, times(1)).addTopCategory(testCategory);
    }

    @Test
    void whenExistingCategoryIsEmpty_whenAddCategoryToExisting_thenCategoryIsAddedAsTopCategory(){
        Category testCategory = new Category("Test", List.of());
        given(categoryRepository.findByName("Test")).willReturn(Mono.empty());
        given(categoryRepository.save(any())).willReturn(Mono.just(testCategory));
        CategoryService spyCategoryService = spy(categoryService);

        Mono<Category> addCategoryToExisting = spyCategoryService.addCategoryToExisting(
                testCategory,
                List.of());
        StepVerifier.create(addCategoryToExisting)
                .expectNext(testCategory)
                .verifyComplete();
        verify(spyCategoryService, times(1)).addTopCategory(testCategory);
    }

    @Test
    void whenExistingCategoryNotExist_whenAddCategoryToExisting_thenThrow(){
        Category testCategory = new Category("Test", List.of());
        given(categoryRepository.findByName("Missing")).willReturn(Mono.empty());

        Mono<Category> categoryMono = categoryService.addCategoryToExisting(testCategory, List.of("Missing"));
        StepVerifier.create(categoryMono)
                .expectErrorMessage("Existing category not found")
                .verify();
    }

    @Test
    void whenExistingCategoryTargetNotExist_whenAddCategoryToExisting_thenThrow(){
        Category testCategory = new Category("Test", List.of());
        Category furnitureCategory = new Category("Furniture", List.of());
        given(categoryRepository.findByName("Furniture")).willReturn(Mono.just(furnitureCategory));

        Mono<Category> categoryMono = categoryService.addCategoryToExisting(testCategory, List.of("Furniture", "Missing"));
        StepVerifier.create(categoryMono)
                .expectErrorMessage("Existing category provided doesn't exist")
                .verify();
    }

    @Test
    void whenExistingCategoryAlreadyExistInTarget_whenAddCategoryToExisting_thenThrow(){
        Category testCategory = new Category("Test", List.of());
        Category furnitureCategory = new Category("Furniture", List.of(testCategory));
        given(categoryRepository.findByName("Furniture")).willReturn(Mono.just(furnitureCategory));

        Mono<Category> categoryMono = categoryService.addCategoryToExisting(testCategory, List.of("Furniture"));
        StepVerifier.create(categoryMono)
                .expectErrorMessage("New category already exists")
                .verify();
    }

    @Test
    void whenAddCategoryToExisting_thenReturnTopCategory(){
        Category testCategory = new Category("Test", List.of());
        Category livingRoomCategory = new Category("Living Room", List.of());
        Category furnitureCategory = new Category("Furniture", List.of(livingRoomCategory));
        given(categoryRepository.findByName("Furniture")).willReturn(Mono.just(furnitureCategory));
        given(categoryRepository.save(any())).willReturn(Mono.just(furnitureCategory));

        Mono<Category> categoryMono = categoryService.addCategoryToExisting(testCategory, List.of("Furniture", "Living Room"));
        StepVerifier.create(categoryMono)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void givenCategoryHierarchyIsValid_whenValidateCategoryHierarchy_thenReturnCategory(){
        Category testCategory = new Category("Test", List.of());
        Category furnitureCategory = new Category("Furniture", List.of(testCategory));
        given(categoryRepository.findByName("Furniture")).willReturn(Mono.just(furnitureCategory));

        Mono<Category> categoryMono = categoryService.validateCategoryHierarchy(List.of("Furniture", "Test"));
        StepVerifier.create(categoryMono)
                .expectNext(testCategory)
                .verifyComplete();
    }

    @Test
    void givenCategoryHierarchyIsOnly1Level_whenValidateCategoryHierarchy_thenReturnCategory(){
        Category furnitureCategory = new Category("Furniture", List.of());
        given(categoryRepository.findByName("Furniture")).willReturn(Mono.just(furnitureCategory));

        Mono<Category> categoryMono = categoryService.validateCategoryHierarchy(List.of("Furniture"));
        StepVerifier.create(categoryMono)
                .expectNext(furnitureCategory)
                .verifyComplete();
    }

    @Test
    void givenMultiLevelCategoryHierarchyIsValid_whenValidateCategoryHierarchy_thenReturnCategory(){
        Category sofaCategory = new Category("Sofa", List.of());
        Category coffeeTableCategory = new Category("Coffee Table", List.of());
        Category livingRoomCategory = new Category("Living Room", List.of(sofaCategory, coffeeTableCategory));
        Category furnitureCategory = new Category("Furniture", List.of(livingRoomCategory));
        given(categoryRepository.findByName("Furniture")).willReturn(Mono.just(furnitureCategory));

        Mono<Category> categoryMono = categoryService.validateCategoryHierarchy(List.of("Furniture", "Living Room", "Sofa"));
        StepVerifier.create(categoryMono)
                .expectNext(sofaCategory)
                .verifyComplete();
    }

    @Test
    void givenCategoryHierarchyIsNull_whenValidateCategoryHierarchy_thenThrow(){
        Mono<Category> categoryMono = categoryService.validateCategoryHierarchy(null);
        StepVerifier.create(categoryMono)
                .expectErrorMessage("Category hierarchy is missing")
                .verify();
    }

    @Test
    void givenCategoryHierarchyIsEmpty_whenValidateCategoryHierarchy_thenThrow(){
        Mono<Category> categoryMono = categoryService.validateCategoryHierarchy(List.of());
        StepVerifier.create(categoryMono)
                .expectErrorMessage("Category hierarchy is missing")
                .verify();
    }

    @Test
    void givenCategoryHierarchyIsInvalid_whenValidateCategoryHierarchy_thenThrow(){
        given(categoryRepository.findByName(anyString())).willReturn(Mono.empty());
        Mono<Category> categoryMono = categoryService.validateCategoryHierarchy(List.of("something"));
        StepVerifier.create(categoryMono)
                .expectErrorMessage("Category not found")
                .verify();
    }
}

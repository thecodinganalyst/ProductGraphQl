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
import java.util.Objects;

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
    void whenGetCategory_shouldReturnCorrectly(){
        Category categoryFromRepo = new Category(
                "1",
                "Furniture",
                List.of(
                        new Category(
                                "2",
                                "Living Room",
                                List.of(new Category("3", "Sofa", List.of())))
                )
        );
        given(categoryRepository.findByName("Furniture")).willReturn(Mono.just(categoryFromRepo));

        StepVerifier.create(categoryService.getCategory(List.of("Furniture", "Living Room")))
                .expectNextMatches(category ->
                        category.getName().equals("Living Room") &&
                        category.getSubCategories().size() == 1 &&
                        Objects.equals(category.getSubCategories().get(0).getName(), "Sofa"))
                .verifyComplete();
    }

    @Test
    void givenCategoryHierarchyIsNull_whenGetCategory_thenThrow(){
        StepVerifier.create(categoryService.getCategory(null))
                .expectErrorMessage("Category hierarchy is missing")
                .verify();
    }

    @Test
    void givenCategoryHierarchyIsEmpty_whenGetCategory_thenThrow(){
        StepVerifier.create(categoryService.getCategory(List.of()))
                .expectErrorMessage("Category hierarchy is missing")
                .verify();
    }

    @Test
    void whenGetCategories_thenCallRepoFindAll(){
        Category cat1 = new Category("1", "Cat 1", List.of());
        Category cat2 = new Category("2", "Cat 2", List.of());
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
        Category test = new Category("1", null, List.of());
        StepVerifier.create(categoryService.addTopCategory(test))
                .expectErrorMessage("Invalid category")
                .verify();
    }

    @Test
    void givenCategoryNameIsEmpty_whenAddTopCategory_thenThrow(){
        Category test = new Category("1", "    ", List.of());
        StepVerifier.create(categoryService.addTopCategory(test))
                .expectErrorMessage("Invalid category")
                .verify();
    }

    @Test
    void givenTopCategoryNameExists_whenAddTopCategory_thenThrow(){
        Category testCategory = new Category("1", "Test", List.of());
        given(categoryRepository.findByName("Test")).willReturn(Mono.just(testCategory));
        StepVerifier.create(categoryService.addTopCategory(testCategory))
                .expectErrorMessage("A category with this name already exists")
                .verify();
    }

    @Test
    void whenAddTopCategory_thenReturnCategory(){
        Category testCategory = new Category("1", "Test", List.of());
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
        Category testCategory = new Category("1", "Test", List.of());
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
        Category testCategory = new Category("1", "Test", List.of());
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
        Category testCategory = new Category("1", "Test", List.of());
        given(categoryRepository.findByName("Missing")).willReturn(Mono.empty());

        Mono<Category> categoryMono = categoryService.addCategoryToExisting(testCategory, List.of("Missing"));
        StepVerifier.create(categoryMono)
                .expectErrorMessage("Existing category not found")
                .verify();
    }

    @Test
    void whenExistingCategoryTargetNotExist_whenAddCategoryToExisting_thenThrow(){
        Category testCategory = new Category("2", "Test", List.of());
        Category furnitureCategory = new Category("1", "Furniture", List.of());
        given(categoryRepository.findByName("Furniture")).willReturn(Mono.just(furnitureCategory));

        Mono<Category> categoryMono = categoryService.addCategoryToExisting(testCategory, List.of("Furniture", "Missing"));
        StepVerifier.create(categoryMono)
                .expectErrorMessage("Existing category provided doesn't exist")
                .verify();
    }

    @Test
    void whenExistingCategoryAlreadyExistInTarget_whenAddCategoryToExisting_thenThrow(){
        Category testCategory = new Category("2", "Test", List.of());
        Category furnitureCategory = new Category("1", "Furniture", List.of(testCategory));
        given(categoryRepository.findByName("Furniture")).willReturn(Mono.just(furnitureCategory));

        Mono<Category> categoryMono = categoryService.addCategoryToExisting(testCategory, List.of("Furniture"));
        StepVerifier.create(categoryMono)
                .expectErrorMessage("New category already exists")
                .verify();
    }

    @Test
    void whenAddCategoryToExisting_thenReturnTopCategory(){
        Category testCategory = new Category("3", "Test", List.of());
        Category livingRoomCategory = new Category("2", "Living Room", List.of());
        Category furnitureCategory = new Category("1", "Furniture", List.of(livingRoomCategory));
        given(categoryRepository.findByName("Furniture")).willReturn(Mono.just(furnitureCategory));
        given(categoryRepository.save(any())).willReturn(Mono.just(furnitureCategory));

        Mono<Category> categoryMono = categoryService.addCategoryToExisting(testCategory, List.of("Furniture", "Living Room"));
        StepVerifier.create(categoryMono)
                .expectNextCount(1)
                .verifyComplete();
    }
}

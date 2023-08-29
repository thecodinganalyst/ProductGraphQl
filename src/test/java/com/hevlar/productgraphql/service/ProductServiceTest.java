package com.hevlar.productgraphql.service;

import com.hevlar.productgraphql.model.Category;
import com.hevlar.productgraphql.model.Product;
import com.hevlar.productgraphql.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    ProductRepository productRepository;

    @Mock
    CategoryService categoryService;

    @InjectMocks
    ProductService productService;

    Category livingRoom = new Category("Living Room", List.of());
    Category kitchen = new Category("Kitchen", List.of());
    Category furniture = new Category("Furniture", List.of(livingRoom, kitchen));
    Product sofa1 = new Product(
            "1",
            "Sofa 1",
            "2 seater sofa",
            List.of(),
            List.of("Furniture", "Living Room"),
            List.of("simple"),
            List.of());
    Product sofa2 = new Product(
            "2",
            "Sofa 2",
            "3 seater sofa",
            List.of(),
            List.of("Furniture", "Living Room"),
            List.of("luxury"),
            List.of());

    Product sofa3 = new Product(
            "3",
            "Sofa 3",
            "L-shaped sofa",
            List.of(),
            List.of("Furniture", "Living Room"),
            List.of("luxury"),
            List.of());

    @Test
    void whenGetProductsByCategory_thenReturnProducts() {
        given(categoryService.validateCategoryHierarchy(anyList())).willReturn(Mono.just(livingRoom));
        given(productRepository.findAllByCategory(anyList())).willReturn(Flux.just(sofa1, sofa2));

        Flux<Product> productFlux = productService.getProductsByCategoryHierarchy(List.of("Furniture", "Living Room"));
        StepVerifier.create(productFlux)
                .expectNext(sofa1, sofa2)
                .verifyComplete();
    }

    @Test
    void givenCategoryHierarchyIsNull_whenGetProductsByCategory_thenReturnProducts() {
        Flux<Product> productFlux = productService.getProductsByCategoryHierarchy(null);
        StepVerifier.create(productFlux)
                .expectErrorMessage("Category hierarchy is empty")
                .verify();

    }

    @Test
    void givenCategoryHierarchyIsEmpty_whenGetProductsByCategory_thenReturnProducts() {
        Flux<Product> productFlux = productService.getProductsByCategoryHierarchy(List.of());
        StepVerifier.create(productFlux)
                .expectErrorMessage("Category hierarchy is empty")
                .verify();

    }

    @Test
    void whenAddProduct_thenReturnProduct(){
        given(productRepository.save(sofa3)).willReturn(Mono.just(sofa3));
        given(categoryService.validateCategoryHierarchy(anyList())).willReturn(Mono.just(livingRoom));
        Mono<Product> productMono = productService.addProduct(sofa3);
        StepVerifier.create(productMono)
                .expectNext(sofa3)
                .verifyComplete();
    }

    @Test
    void givenProductIsNull_whenAddProduct_thenReturnProduct(){
        StepVerifier.create(productService.addProduct(null))
                .expectErrorMessage("Product is null")
                .verify();
    }

    @Test
    void givenProductCategoryIsNull_whenAddProduct_thenReturnProduct(){
        Product nullCategoryProduct = new Product(
                "3",
                "Sofa 3",
                "L-shaped sofa",
                List.of(),
                null,
                List.of("luxury"),
                List.of());
        StepVerifier.create(productService.addProduct(nullCategoryProduct))
                .expectErrorMessage("Product category is null")
                .verify();
    }

    @Test
    void givenProductCategoryIsEmpty_whenAddProduct_thenReturnProduct(){
        Product emptyCategoryProduct = new Product(
                "3",
                "Sofa 3",
                "L-shaped sofa",
                List.of(),
                List.of(),
                List.of("luxury"),
                List.of());
        StepVerifier.create(productService.addProduct(emptyCategoryProduct))
                .expectErrorMessage("Product category is null")
                .verify();
    }

    @Test
    void givenProductCategoryIsInvalid_whenAddProduct_thenReturnProduct(){
        Product invalidCategoryProduct = new Product(
                "3",
                "Sofa 3",
                "L-shaped sofa",
                List.of(),
                List.of("Something", "Else"),
                List.of("luxury"),
                List.of());
        given(categoryService.validateCategoryHierarchy(anyList()))
                .willReturn(Mono.error(() -> new IllegalArgumentException("Category not found")));
        StepVerifier.create(productService.addProduct(invalidCategoryProduct))
                .expectErrorMessage("Category not found")
                .verify();
    }
}

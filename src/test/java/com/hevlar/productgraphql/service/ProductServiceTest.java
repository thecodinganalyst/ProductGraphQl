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

import static org.mockito.ArgumentMatchers.any;
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

    Category livingRoom = new Category("1", "Living Room", List.of());
    Category kitchen = new Category("2", "Kitchen", List.of());
    Category furniture = new Category("3", "Furniture", List.of(livingRoom, kitchen));
    Product sofa1 = new Product(
            "1",
            "Sofa 1",
            "2 seater sofa",
            List.of(),
            livingRoom,
            List.of("simple"),
            List.of());
    Product sofa2 = new Product(
            "2",
            "Sofa 2",
            "3 seater sofa",
            List.of(),
            livingRoom,
            List.of("luxury"),
            List.of());

    @Test
    void whenGetProductsByCategory_thenReturnProducts() {
        given(categoryService.getCategory(anyList())).willReturn(Mono.just(livingRoom));
        given(productRepository.findAllByCategory(any())).willReturn(Flux.just(sofa1, sofa2));

        Flux<Product> productFlux = productService.getProductsByCategory(List.of("Furniture", "Living Room"));
        StepVerifier.create(productFlux)
                .expectNext(sofa1, sofa2)
                .verifyComplete();
    }
}

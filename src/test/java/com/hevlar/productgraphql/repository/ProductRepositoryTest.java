package com.hevlar.productgraphql.repository;

import com.hevlar.productgraphql.model.Category;
import com.hevlar.productgraphql.model.Product;
import com.hevlar.productgraphql.model.ProductStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

@DataMongoTest
@Testcontainers
@ContextConfiguration(classes = {ReactiveMongoTestConfiguration.class})
class ProductRepositoryTest {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CategoryRepository categoryRepository;

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
            List.of(),
            ProductStatus.AVAILABLE);
    Product sofa2 = new Product(
            "2",
            "Sofa 2",
            "3 seater sofa",
            List.of(),
            List.of("Furniture", "Living Room"),
            List.of("luxury"),
            List.of(),
            ProductStatus.AVAILABLE);

    @BeforeEach
    void setup(){
        categoryRepository.saveAll(List.of(furniture, livingRoom, kitchen)).blockLast();
        productRepository.saveAll(List.of(sofa1, sofa2)).blockLast();
    }

    @AfterEach
    public void tearDown() {
        categoryRepository.deleteAll().block();
        productRepository.deleteAll().block();
    }

    @Test
    void findAllByCategory() {
        Flux<Product> productFlux = productRepository.findAllByCategory(List.of("Furniture", "Living Room"));
        StepVerifier.create(productFlux)
                .expectNext(sofa1, sofa2)
                .verifyComplete();
    }

    @Test
    void findById(){
        Mono<Product> productMono = productRepository.findById("2");
        StepVerifier.create(productMono)
                .expectNext(sofa2)
                .verifyComplete();
    }
}

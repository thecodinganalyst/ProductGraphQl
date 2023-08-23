package com.hevlar.productgraphql.repository;

import com.hevlar.productgraphql.model.Category;
import com.hevlar.productgraphql.model.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
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
        Flux<Product> productFlux = productRepository.findAllByCategory(livingRoom);
        StepVerifier.create(productFlux)
                .expectNext(sofa1, sofa2)
                .verifyComplete();
    }
}

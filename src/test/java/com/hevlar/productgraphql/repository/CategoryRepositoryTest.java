package com.hevlar.productgraphql.repository;

import com.hevlar.productgraphql.model.Category;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@DataMongoTest
@Testcontainers
@ContextConfiguration(classes = {ReactiveMongoTestConfiguration.class})
class CategoryRepositoryTest {

    @Autowired
    CategoryRepository categoryRepository;

    @BeforeEach
    void setup(){
        categoryRepository.saveAll(Arrays.asList(
                new Category("1", "Furniture", List.of()),
                new Category("2", "Electronics", List.of())
        )).blockLast();
    }

    @AfterEach
    public void tearDown() {
        categoryRepository.deleteAll().block();
    }

    @Test
    void findByName() {
        StepVerifier.create(categoryRepository.findByName("Electronics"))
                .expectNextMatches(category -> category.getName().equals("Electronics"))
                .verifyComplete();
    }
}

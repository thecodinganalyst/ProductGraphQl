package com.hevlar.productgraphql.controller;

import com.hevlar.productgraphql.MongoDBTestContainerConfig;
import com.hevlar.productgraphql.model.Category;
import com.hevlar.productgraphql.model.Product;
import com.hevlar.productgraphql.repository.CategoryRepository;
import com.hevlar.productgraphql.repository.ProductRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureWebTestClient
@AutoConfigureHttpGraphQlTester
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers
@ContextConfiguration(classes = MongoDBTestContainerConfig.class)
public class ProductControllerIntegrationTest {

    @Autowired
    HttpGraphQlTester httpGraphQlTester;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ProductRepository productRepository;

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

    @BeforeAll
    void setup(){
        categoryRepository.save(furniture).block();
        productRepository.saveAll(List.of(sofa1, sofa2)).blockLast();
    }

    @AfterAll
    public void tearDown() {
        categoryRepository.deleteAll().block();
        productRepository.deleteAll().block();
    }

    @Test
    void whenGetProductsByCategory_thenReturnProducts(){
        List<Product> productList = this.httpGraphQlTester.document("""
                query {
                    getProductsByCategory(categoryHierarchy: ["Furniture", "Living Room"]){
                        id
                        name
                        description
                        imageUrls
                        category {
                            name
                        }
                        tags
                    }
                }
                """)
                .execute()
                .errors()
                .verify()
                .path("getProductsByCategory")
                .entityList(Product.class)
                .get();

        assertThat(productList.size()).isEqualTo(2);
    }

}

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

    Category livingRoom = new Category("Living Room", List.of());
    Category kitchen = new Category("Kitchen", List.of());
    Category furniture = new Category("Furniture", List.of(livingRoom, kitchen));
    Product sofa1 = new Product(
            "Sofa 1",
            "2 seater sofa",
            List.of(),
            List.of("Furniture", "Living Room"),
            List.of("simple"),
            List.of());
    Product sofa2 = new Product(
            "Sofa 2",
            "3 seater sofa",
            List.of(),
            List.of("Furniture", "Living Room"),
            List.of("luxury"),
            List.of());

    Product sofa3 = new Product(
            "Sofa 3",
            "L-shaped sofa",
            List.of(),
            List.of("Furniture", "Living Room"),
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
                        category
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

    @Test
    void whenAddProduct_thenReturnProductAdded(){
        Product product = this.httpGraphQlTester
                .document("""
                            mutation {
                                addProduct(newProduct: {
                                    name: "Sofa 3",
                                    description: "L-shaped sofa",
                                    imageUrls: [],
                                    category: ["Furniture", "Living Room"],
                                    tags: "luxury"
                                }){
                                    id
                                    name
                                    description
                                    imageUrls
                                    category
                                    tags
                                }
                            }
                        """)
                .execute()
                .errors()
                .verify()
                .path("addProduct")
                .entity(Product.class)
                .get();
        assertThat(product.getName()).isEqualTo("Sofa 3");
        assertThat(product.getDescription()).isEqualTo("L-shaped sofa");
        assertThat(product.getImageUrls().size()).isEqualTo(0);
        assertThat(product.getCategory()).isEqualTo(List.of("Furniture", "Living Room"));
        assertThat(product.getTags()).isEqualTo(List.of("luxury"));
    }

}

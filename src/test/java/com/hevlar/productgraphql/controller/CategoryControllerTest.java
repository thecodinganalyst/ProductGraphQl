package com.hevlar.productgraphql.controller;

import com.hevlar.productgraphql.MongoDBTestContainerConfig;
import com.hevlar.productgraphql.ProductGraphQlApplication;
import com.hevlar.productgraphql.model.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ProductGraphQlApplication.class)
@AutoConfigureWebTestClient
@AutoConfigureHttpGraphQlTester
@Testcontainers
@ContextConfiguration(classes = MongoDBTestContainerConfig.class)
public class CategoryControllerTest {

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    HttpGraphQlTester httpGraphQlTester;

    @Test
    public void testGetCategories() {
        Category category = this.httpGraphQlTester.document(
                        """
                        mutation {
                            addTopCategory(category: {
                                name: "Furniture",
                                subCategories: [
                                    {name: "Living Room"},
                                    {name: "Kitchen"}
                                ]
                            }){
                                name
                                subCategories{
                                    name
                                }
                            }
                        }
                        """
                )
                .execute()
                .errors()
                .verify()
                .path("addTopCategory")
                .entity(Category.class)
                .get();
        assertThat(category).hasFieldOrPropertyWithValue("name", "Furniture");
    }
}

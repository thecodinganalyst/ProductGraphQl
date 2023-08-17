package com.hevlar.productgraphql.controller;

import com.hevlar.productgraphql.MongoDBTestContainerConfig;
import com.hevlar.productgraphql.model.Category;
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
public class CategoryControllerIntegrationTest {

    @Autowired
    HttpGraphQlTester httpGraphQlTester;

    @Test
    @Order(1)
    public void testAddTopCategory() {
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
        assertThat(category).hasFieldOrProperty("subCategories");
        assertThat(category.subCategories()).hasSize(2);
        List<String> subCategoryNames = category.subCategories().stream().map(Category::name).toList();
        assertThat(subCategoryNames).hasSameElementsAs(List.of("Kitchen", "Living Room"));
    }

    @Test
    @Order(2)
    public void testAddCategoryToExisting() {
        Category category = this.httpGraphQlTester.document(
                        """
                        mutation {
                            addCategoryToExisting(
                                newCategory: {
                                    name: "Sofa"
                                    subCategories: [
                                        { name: "2-seater" },
                                        { name: "3-seater" },
                                        { name: "L-Shape" }
                                    ]
                                },
                                existingCategory: ["Furniture", "Living Room"]
                            ){
                                name
                                subCategories {
                                    name
                                }
                            }
                        }
                        """
                )
                .execute()
                .errors()
                .verify()
                .path("addCategoryToExisting")
                .entity(Category.class)
                .get();


        assertThat(category).hasFieldOrPropertyWithValue("name", "Furniture");
//        Category livingRoomCategory = category.subCategories()
//                .stream()
//                .filter(cat -> cat.name().equals("Living Room"))
//                .findFirst()
//                .get();
//        Category sofaCategory = livingRoomCategory.subCategories().get(0);
//        assertThat(sofaCategory).hasFieldOrPropertyWithValue("name", "Sofa");
//        assertThat(sofaCategory.subCategories()).hasSize(3);
//        List<String> sofaSubCategoryNames = sofaCategory.subCategories().stream().map(Category::name).toList();
//        assertThat(sofaSubCategoryNames).hasSameElementsAs(List.of("2-seater", "3-seater", "L-Shape"));

    }
}

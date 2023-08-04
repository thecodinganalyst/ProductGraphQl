package com.hevlar.productgraphql;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@ContextConfiguration(classes = MongoDBTestContainerConfig.class)
class ProductGraphQlApplicationTests {

    @Test
    void contextLoads() {
    }

}

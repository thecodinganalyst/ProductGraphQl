package com.hevlar.productgraphql;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@Slf4j
@ContextConfiguration(classes = MongoDBTestContainerConfig.class)
class ProductGraphQlApplicationTests {

    @Autowired
    Environment environment;

    @Test
    void contextLoads() {
        log.info(environment.getProperty("spring.data.mongodb.port"));
    }

}

package com.hevlar.productgraphql.repository;

import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory;
import org.testcontainers.containers.MongoDBContainer;

@TestConfiguration
public class ReactiveMongoTestConfiguration {

    @Bean
    @ServiceConnection
    MongoDBContainer mongoDBContainer() {
        return new MongoDBContainer("mongo:latest");
    }

    @Bean
    ReactiveMongoDatabaseFactory reactiveMongoDatabaseFactory(MongoDBContainer mongoDBContainer) {
        return new SimpleReactiveMongoDatabaseFactory(
                MongoClients.create(mongoDBContainer.getReplicaSetUrl("ProductGraphQl")),
                "ProductGraphQl"
        );
    }
}

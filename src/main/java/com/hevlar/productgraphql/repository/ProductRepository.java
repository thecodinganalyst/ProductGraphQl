package com.hevlar.productgraphql.repository;

import com.hevlar.productgraphql.model.Category;
import com.hevlar.productgraphql.model.Product;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ProductRepository extends ReactiveMongoRepository<Product, String> {
    Flux<Product> findAllByCategory(Category category);
}

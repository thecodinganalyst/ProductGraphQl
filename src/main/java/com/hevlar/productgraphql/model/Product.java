package com.hevlar.productgraphql.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public record Product(
    @Id
    String id,
    @Indexed
    String name,
    String description,
    String[] imageUrls,
    @Indexed
    String category,
    @Indexed
    String[] tags,
    Variant[] variants
) { }

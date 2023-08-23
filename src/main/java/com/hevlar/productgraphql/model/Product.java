package com.hevlar.productgraphql.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
public record Product(
    @Id
    String id,
    @Indexed
    String name,
    String description,
    List<String> imageUrls,
    @Indexed
    Category category,
    @Indexed
    List<String> tags,
    List<Variant> variants
) { }

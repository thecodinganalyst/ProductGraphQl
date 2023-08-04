package com.hevlar.productgraphql.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public record VariantType(
        String name,
        String[] options
) {
}

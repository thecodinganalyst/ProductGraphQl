package com.hevlar.productgraphql.model;

public record Variant(
        String name,
        String[] options
) {
}

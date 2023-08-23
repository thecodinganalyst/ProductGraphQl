package com.hevlar.productgraphql.model;

import java.util.List;

public record Variant(
        String name,
        List<Attribute> attributeList
) {
}

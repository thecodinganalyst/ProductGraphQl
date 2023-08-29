package com.hevlar.productgraphql.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.is;

class ProductTest {
    @Test
    void hasVariantOfName() {
        Variant variant1 = new Variant("Variant 1", List.of());
        Variant variant2 = new Variant("Variant 2", List.of());
        Product product = new Product(
                "1",
                "Product",
                "Description",
                List.of(),
                List.of("Category"),
                List.of("tags"),
                List.of(variant1, variant2),
                ProductStatus.AVAILABLE
        );
        assertThat(product.hasVariantOfName("Variant 1"), is(true));
        assertThat(product.hasVariantOfName("Something"), is(false));
    }
}

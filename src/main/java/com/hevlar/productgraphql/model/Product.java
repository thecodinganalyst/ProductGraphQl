package com.hevlar.productgraphql.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@Data
@NoArgsConstructor
public class Product{
    @Id
    String id;
    @Indexed
    String name;
    String description;
    List<String> imageUrls;
    @Indexed
    List<String> category;
    @Indexed
    List<String> tags;
    List<Variant> variants;
    ProductStatus status;

    public Product(String id, String name, String description, List<String> imageUrls, List<String> category, List<String> tags, List<Variant> variants, ProductStatus status){
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrls = imageUrls == null ? List.of() : imageUrls;
        this.category = category;
        this.tags = tags == null ? List.of() : tags;
        this.variants = variants == null ? List.of() : variants;
        this.status = status;
    }

    public Product(String name, String description, List<String> imageUrls, List<String> category, List<String> tags, List<Variant> variants, ProductStatus status){
        this.name = name;
        this.description = description;
        this.imageUrls = imageUrls == null ? List.of() : imageUrls;
        this.category = category;
        this.tags = tags == null ? List.of() : tags;
        this.variants = variants == null ? List.of() : variants;
        this.status = status;
    }

    public boolean hasVariantOfName(String name){
        return variants.stream().anyMatch(variant -> variant.name().equals(name));
    }
}

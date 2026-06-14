package org.example.mongodemo.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "products")
public class Product {

    @Id
    private String id;

    @NotBlank
    private String name;

    @NotBlank
    private String category;

    @NotNull
    @Min(0)
    private Long availableStock;

    @NotNull
    @Min(0)
    private Long unitsSold;

    public Product() {
    }

    public Product(String id, String name, String category, Long availableStock, Long unitsSold) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.availableStock = availableStock;
        this.unitsSold = unitsSold;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Long getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(Long availableStock) {
        this.availableStock = availableStock;
    }

    public Long getUnitsSold() {
        return unitsSold;
    }

    public void setUnitsSold(Long unitsSold) {
        this.unitsSold = unitsSold;
    }
}


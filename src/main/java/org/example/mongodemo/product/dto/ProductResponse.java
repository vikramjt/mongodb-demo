package org.example.mongodemo.product.dto;

public record ProductResponse(
        String id,
        String name,
        String category,
        Long availableStock,
        Long unitsSold
) {
}


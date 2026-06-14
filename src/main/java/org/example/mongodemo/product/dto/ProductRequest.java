package org.example.mongodemo.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductRequest(
        @NotBlank String name,
        @NotBlank String category,
        @NotNull @Min(0) Long availableStock,
        @NotNull @Min(0) Long unitsSold
) {
}


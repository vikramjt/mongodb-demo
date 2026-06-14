package org.example.mongodemo.product.dto;

import jakarta.validation.constraints.NotNull;

public record UnitsSoldOperationRequest(@NotNull Long delta) {
}


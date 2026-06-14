package org.example.mongodemo.product.dto;

import java.util.List;

public record UnitsSoldSummaryResponse(
        long totalUnitsSold,
        double averageUnitsSold,
        long maxUnitsSold,
        long minUnitsSold,
        long productCount,
        List<CategoryUnitsSoldResponse> byCategory
) {
}


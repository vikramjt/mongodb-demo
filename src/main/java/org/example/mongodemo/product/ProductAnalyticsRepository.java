package org.example.mongodemo.product;

import org.example.mongodemo.product.dto.CategoryUnitsSoldResponse;
import org.example.mongodemo.product.dto.UnitsSoldSummaryResponse;

import java.util.List;

public interface ProductAnalyticsRepository {

    UnitsSoldSummaryResponse getUnitsSoldSummary();

    List<CategoryUnitsSoldResponse> getUnitsSoldByCategory();
}


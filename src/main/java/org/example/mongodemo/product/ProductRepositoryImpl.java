package org.example.mongodemo.product;

import org.example.mongodemo.product.dto.CategoryUnitsSoldResponse;
import org.example.mongodemo.product.dto.UnitsSoldSummaryResponse;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
public class ProductRepositoryImpl implements ProductAnalyticsRepository {

    private final MongoTemplate mongoTemplate;

    public ProductRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public UnitsSoldSummaryResponse getUnitsSoldSummary() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("unitsSold").ne(null)),
                Aggregation.group()
                        .sum("unitsSold").as("totalUnitsSold")
                        .avg("unitsSold").as("averageUnitsSold")
                        .max("unitsSold").as("maxUnitsSold")
                        .min("unitsSold").as("minUnitsSold")
                        .count().as("productCount")
        );

        AggregationResults<UnitsSoldSummaryProjection> results =
                mongoTemplate.aggregate(aggregation, "products", UnitsSoldSummaryProjection.class);

        UnitsSoldSummaryProjection summary = results.getUniqueMappedResult();

        if (summary == null) {
            return new UnitsSoldSummaryResponse(0, 0, 0, 0, 0, Collections.emptyList());
        }

        return new UnitsSoldSummaryResponse(
                summary.getTotalUnitsSold(),
                summary.getAverageUnitsSold(),
                summary.getMaxUnitsSold(),
                summary.getMinUnitsSold(),
                summary.getProductCount(),
                getUnitsSoldByCategory()
        );
    }

    @Override
    public List<CategoryUnitsSoldResponse> getUnitsSoldByCategory() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("unitsSold").ne(null)),
                Aggregation.group("category").sum("unitsSold").as("unitsSoldSum"),
                Aggregation.project("unitsSoldSum").and("_id").as("category"),
                Aggregation.sort(Sort.by(Sort.Direction.DESC, "unitsSoldSum"))
        );

        AggregationResults<CategoryUnitsSoldResponse> results =
                mongoTemplate.aggregate(aggregation, "products", CategoryUnitsSoldResponse.class);

        return results.getMappedResults();
    }

    private static class UnitsSoldSummaryProjection {

        private long totalUnitsSold;
        private double averageUnitsSold;
        private long maxUnitsSold;
        private long minUnitsSold;
        private long productCount;

        public long getTotalUnitsSold() {
            return totalUnitsSold;
        }

        public void setTotalUnitsSold(long totalUnitsSold) {
            this.totalUnitsSold = totalUnitsSold;
        }

        public double getAverageUnitsSold() {
            return averageUnitsSold;
        }

        public void setAverageUnitsSold(double averageUnitsSold) {
            this.averageUnitsSold = averageUnitsSold;
        }

        public long getMaxUnitsSold() {
            return maxUnitsSold;
        }

        public void setMaxUnitsSold(long maxUnitsSold) {
            this.maxUnitsSold = maxUnitsSold;
        }

        public long getMinUnitsSold() {
            return minUnitsSold;
        }

        public void setMinUnitsSold(long minUnitsSold) {
            this.minUnitsSold = minUnitsSold;
        }

        public long getProductCount() {
            return productCount;
        }

        public void setProductCount(long productCount) {
            this.productCount = productCount;
        }
    }
}


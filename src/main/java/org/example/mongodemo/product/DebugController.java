package org.example.mongodemo.product;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    private final MongoTemplate mongoTemplate;
    private final ProductRepository productRepository;

    public DebugController(MongoTemplate mongoTemplate, ProductRepository productRepository) {
        this.mongoTemplate = mongoTemplate;
        this.productRepository = productRepository;
    }

    @GetMapping("/count")
    public Map<String, Object> getCount() {
        Map<String, Object> result = new HashMap<>();
        
        // Count via MongoTemplate
        long mongoTemplateCount = mongoTemplate.count(new Query(), Product.class);
        
        // Count via Repository
        long repositoryCount = productRepository.count();
        
        result.put("mongoTemplateCount", mongoTemplateCount);
        result.put("repositoryCount", repositoryCount);
        
        return result;
    }

    @GetMapping("/validate-all-records")
    public Map<String, Object> validateAllRecords() {
        Map<String, Object> result = new HashMap<>();
        List<Product> allProducts = productRepository.findAll();
        List<Map<String, Object>> invalidRecords = new ArrayList<>();
        int validCount = 0;

        for (Product product : allProducts) {
            Map<String, Object> issues = new HashMap<>();
            boolean isInvalid = false;

            // Check for null/blank name
            if (product.getName() == null || product.getName().isBlank()) {
                issues.put("name", "Name is null or blank");
                isInvalid = true;
            }

            // Check for null/blank category
            if (product.getCategory() == null || product.getCategory().isBlank()) {
                issues.put("category", "Category is null or blank");
                isInvalid = true;
            }

            // Check for null availableStock
            if (product.getAvailableStock() == null) {
                issues.put("availableStock", "Available stock is null");
                isInvalid = true;
            } else if (product.getAvailableStock() < 0) {
                issues.put("availableStock", "Available stock is negative: " + product.getAvailableStock());
                isInvalid = true;
            }

            // Check for null unitsSold
            if (product.getUnitsSold() == null) {
                issues.put("unitsSold", "Units sold is null");
                isInvalid = true;
            } else if (product.getUnitsSold() < 0) {
                issues.put("unitsSold", "Units sold is negative: " + product.getUnitsSold());
                isInvalid = true;
            }

            if (isInvalid) {
                Map<String, Object> invalidRecord = new HashMap<>();
                invalidRecord.put("id", product.getId());
                invalidRecord.put("name", product.getName());
                invalidRecord.put("category", product.getCategory());
                invalidRecord.put("availableStock", product.getAvailableStock());
                invalidRecord.put("unitsSold", product.getUnitsSold());
                invalidRecord.put("issues", issues);
                invalidRecords.add(invalidRecord);
            } else {
                validCount++;
            }
        }

        result.put("totalRecords", allProducts.size());
        result.put("validRecords", validCount);
        result.put("invalidRecordCount", invalidRecords.size());
        result.put("invalidRecords", invalidRecords);

        return result;
    }

    @GetMapping("/fix-invalid-records")
    public Map<String, Object> fixInvalidRecords() {
        Map<String, Object> result = new HashMap<>();
        List<Product> allProducts = productRepository.findAll();
        List<String> deletedIds = new ArrayList<>();

        for (Product product : allProducts) {
            boolean hasIssue = false;

            // Check for any invalid data
            if (product.getName() == null || product.getName().isBlank() ||
                product.getCategory() == null || product.getCategory().isBlank() ||
                product.getAvailableStock() == null || product.getAvailableStock() < 0 ||
                product.getUnitsSold() == null || product.getUnitsSold() < 0) {
                hasIssue = true;
            }

            if (hasIssue) {
                productRepository.delete(product);
                deletedIds.add(product.getId());
            }
        }

        result.put("deletedInvalidRecordCount", deletedIds.size());
        result.put("deletedIds", deletedIds);
        result.put("remainingRecords", productRepository.count());

        return result;
    }
}


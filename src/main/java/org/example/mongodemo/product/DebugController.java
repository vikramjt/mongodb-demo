package org.example.mongodemo.product;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
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
}


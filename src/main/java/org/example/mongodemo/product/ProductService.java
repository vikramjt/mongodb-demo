package org.example.mongodemo.product;

import org.example.mongodemo.common.ResourceNotFoundException;
import org.example.mongodemo.product.dto.ProductRequest;
import org.example.mongodemo.product.dto.ProductResponse;
import org.example.mongodemo.product.dto.UnitsSoldSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponse create(ProductRequest request) {
        Product product = toEntity(request);
        Product saved = productRepository.save(product);
        return toResponse(saved);
    }

    public ProductResponse getById(String id) {
        Product product = findById(id);
        return toResponse(product);
    }

    public Page<ProductResponse> list(int page, int size, String sortBy, String direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        return productRepository.findAll(pageable).map(this::toResponse);
    }

    public ProductResponse update(String id, ProductRequest request) {
        Product existing = findById(id);
        existing.setName(request.name());
        existing.setCategory(request.category());
        existing.setAvailableStock(request.availableStock());
        existing.setUnitsSold(request.unitsSold());

        Product updated = productRepository.save(existing);
        return toResponse(updated);
    }

    public void delete(String id) {
        Product existing = findById(id);
        productRepository.delete(existing);
    }

    public ProductResponse adjustUnitsSold(String id, long delta) {
        Product existing = findById(id);
        long updatedUnitsSold = existing.getUnitsSold() + delta;

        if (updatedUnitsSold < 0) {
            throw new IllegalArgumentException("unitsSold cannot be negative after operation");
        }

        existing.setUnitsSold(updatedUnitsSold);
        Product updated = productRepository.save(existing);
        return toResponse(updated);
    }

    public UnitsSoldSummaryResponse getUnitsSoldSummary() {
        return productRepository.getUnitsSoldSummary();
    }

    private Product findById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found for id: " + id));
    }

    private Product toEntity(ProductRequest request) {
        return new Product(
                null,
                request.name(),
                request.category(),
                request.availableStock(),
                request.unitsSold()
        );
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getCategory(),
                product.getAvailableStock(),
                product.getUnitsSold()
        );
    }
}


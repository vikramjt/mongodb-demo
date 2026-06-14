package org.example.mongodemo.product;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.mongodemo.product.dto.ProductRequest;
import org.example.mongodemo.product.dto.ProductResponse;
import org.example.mongodemo.product.dto.UnitsSoldOperationRequest;
import org.example.mongodemo.product.dto.UnitsSoldSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "CRUD, pagination, and unitsSold analytics endpoints")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a product")
    public ProductResponse create(@Valid @RequestBody ProductRequest request) {
        return productService.create(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a product by id")
    public ProductResponse getById(@PathVariable String id) {
        return productService.getById(id);
    }

    @GetMapping
    @Operation(summary = "List products with pagination")
    public Page<ProductResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction
    ) {
        return productService.list(page, size, sortBy, direction);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a product")
    public ProductResponse update(@PathVariable String id, @Valid @RequestBody ProductRequest request) {
        return productService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a product")
    public void delete(@PathVariable String id) {
        productService.delete(id);
    }

    @PostMapping("/{id}/units-sold/adjust")
    @Operation(summary = "Adjust unitsSold by delta (positive or negative)")
    public ProductResponse adjustUnitsSold(
            @PathVariable String id,
            @Valid @RequestBody UnitsSoldOperationRequest request
    ) {
        return productService.adjustUnitsSold(id, request.delta());
    }

    @GetMapping("/analytics/units-sold")
    @Operation(summary = "Aggregate unitsSold across all products and by category")
    public UnitsSoldSummaryResponse getUnitsSoldSummary() {
        return productService.getUnitsSoldSummary();
    }
}


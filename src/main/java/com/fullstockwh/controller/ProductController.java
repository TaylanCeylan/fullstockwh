package com.fullstockwh.controller;

import com.fullstockwh.dto.request.ProductCreateRequest;
import com.fullstockwh.dto.request.ProductUpdateRequest;
import com.fullstockwh.dto.response.ProductResponse;
import com.fullstockwh.service.IProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController
{
    private final IProductService productService;

    //Adding a product
    @PostMapping
    public ResponseEntity<ProductResponse> create(@RequestBody ProductCreateRequest request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }

    //Updating a product
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductUpdateRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    //Deleting a product
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    //Getting all products
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAll() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    //Getting product by ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

}

package com.fullstockwh.controller;

import com.fullstockwh.dto.request.ProductCreateRequest;
import com.fullstockwh.dto.request.ProductUpdateRequest;
import com.fullstockwh.dto.response.ProductResponse;
import com.fullstockwh.service.IProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController
{
    private final IProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAll() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    //Adding new product
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        return new ResponseEntity<>(productService.createProduct(request), HttpStatus.CREATED);
    }

    //search bar for products
    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(@RequestParam String keyword) {
        return ResponseEntity.ok(productService.searchProducts(keyword));
    }

    //Product Updating
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    //Stock Updating
    @PutMapping("/variant/{variantId}/stock")
    public ResponseEntity<ProductResponse> updateVariantStock(
            @PathVariable Long variantId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(productService.updateVariantStock(variantId, quantity));
    }

    //Product Delete (Soft Delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build(); // 204 No Content döner
    }
}

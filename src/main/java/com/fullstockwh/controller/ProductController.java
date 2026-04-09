package com.fullstockwh.controller;

import com.fullstockwh.dto.request.ProductRequest;
import com.fullstockwh.dto.response.ProductResponse;
import com.fullstockwh.enums.Color;
import com.fullstockwh.enums.Size;
import com.fullstockwh.service.IProductService;
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

    @PostMapping("/create")
    public ResponseEntity<ProductResponse> create(@RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }

    @GetMapping("/list")
    public ResponseEntity<List<ProductResponse>> getAll() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> search(@RequestParam String name) {
        return ResponseEntity.ok(productService.searchProducts(name));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<ProductResponse>> filter(@RequestParam Color color, @RequestParam Size size) {
        return ResponseEntity.ok(productService.filterProducts(color, size));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Ürün başarıyla silindi.");
    }
}

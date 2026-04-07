package com.fullstockwh.controller;

import com.fullstockwh.entity.Product;
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

    @GetMapping
    public ResponseEntity<List<Product>> getAll()
    {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @PostMapping
    public ResponseEntity<Product> create(@RequestBody Product product)
    {
        return ResponseEntity.ok(productService.saveProduct(product));
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<Product> updateStock(@PathVariable Long id, @RequestParam Integer qty)
    {
        return ResponseEntity.ok(productService.updateStock(id, qty));
    }
}

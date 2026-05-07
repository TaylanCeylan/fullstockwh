package com.fullstockwh.product.product_variant;

import com.fullstockwh.product.product_variant.dto.VariantCreateRequest;
import com.fullstockwh.product.product_variant.dto.VariantUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/variants")
@RequiredArgsConstructor
public class VariantController
{
    private final VariantService variantService;

    //Adding a product variant
    @PostMapping
    public ResponseEntity<String> addVariant(@Valid @RequestBody VariantCreateRequest request) {
        variantService.addVariantToProduct(request);
        return ResponseEntity.ok("Variant added.");
    }

    //Updating a product variant
    @PutMapping("/{id}")
    public ResponseEntity<String> updateVariant(@PathVariable Long id, @Valid @RequestBody VariantUpdateRequest request) {
        variantService.updateVariant(request, id);
        return ResponseEntity.ok("Variant  updated.");
    }

    //Deleting a product variant
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVariant(@PathVariable Long id) {
        variantService.deleteVariant(id);
        return ResponseEntity.ok("Variant deleted.");
    }
}

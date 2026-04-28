package com.fullstockwh.product.product_variant;

import com.fullstockwh.product.product_variant.dto.VariantCreateRequest;
import com.fullstockwh.product.product_variant.dto.VariantUpdateRequest;
import com.fullstockwh.product.product_variant.dto.VariantResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

public interface VariantService
{
    @PreAuthorize("hasAnyRole('ADMIN')")
    void addVariantToProduct(VariantCreateRequest request);

    @PreAuthorize("hasAnyRole('ADMIN')")
    void updateVariant(VariantUpdateRequest request, Long id);

    @PreAuthorize("hasAnyRole('ADMIN')")
    void deleteVariant(Long variantId);

    VariantResponse mapToVariantResponse(ProductVariant variant);

    List<VariantResponse> getVariantsByProductId(Long productId);
}

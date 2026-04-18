package com.fullstockwh.service;

import com.fullstockwh.dto.request.VariantCreateRequest;
import com.fullstockwh.dto.request.VariantUpdateRequest;
import com.fullstockwh.dto.response.VariantResponse;
import com.fullstockwh.entity.ProductVariant;
import org.springframework.security.access.prepost.PreAuthorize;

public interface IVariantService
{
    @PreAuthorize("hasAnyRole('ADMIN')")
    void addVariantToProduct(VariantCreateRequest request);

    @PreAuthorize("hasAnyRole('ADMIN')")
    void updateVariant(VariantUpdateRequest request, Long id);

    @PreAuthorize("hasAnyRole('ADMIN')")
    void deleteVariant(Long variantId);

    VariantResponse mapToVariantResponse(ProductVariant variant);

}

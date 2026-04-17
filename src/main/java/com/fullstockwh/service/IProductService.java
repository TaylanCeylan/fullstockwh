package com.fullstockwh.service;

import com.fullstockwh.dto.request.ProductCreateRequest;
import com.fullstockwh.dto.request.ProductUpdateRequest;
import com.fullstockwh.dto.response.ProductResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;


public interface IProductService
{
    @PreAuthorize("hasAnyRole('ADMIN')")
    ProductResponse createProduct(ProductCreateRequest request);

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    ProductResponse updateVariantStock(Long variantId, Integer quantity);

    @PreAuthorize("hasAnyRole('ADMIN')")
    ProductResponse updateProduct(Long id, ProductUpdateRequest request);

    @PreAuthorize("hasAnyRole('ADMIN')")
    void deleteProduct(Long id);

    List<ProductResponse> getAllProducts();

    List<ProductResponse> searchProducts(String keyword);
}

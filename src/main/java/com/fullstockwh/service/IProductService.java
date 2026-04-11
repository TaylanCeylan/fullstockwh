package com.fullstockwh.service;

import com.fullstockwh.dto.request.ProductCreateRequest;
import com.fullstockwh.dto.response.ProductResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;


public interface IProductService
{
    @PreAuthorize("hasAnyRole('ADMIN')")
    ProductResponse createProduct(ProductCreateRequest request);

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    ProductResponse updateStock(Long id, Integer quantity);

    List<ProductResponse> getAllProducts();
}

package com.fullstockwh.product;

import com.fullstockwh.product.dto.ProductCreateRequest;
import com.fullstockwh.product.dto.ProductUpdateRequest;
import com.fullstockwh.product.dto.ProductResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

public interface ProductService
{
    @PreAuthorize("hasAnyRole('ADMIN')")
    ProductResponse createProduct(ProductCreateRequest request);

    @PreAuthorize("hasAnyRole('ADMIN')")
    ProductResponse updateProduct(Long id, ProductUpdateRequest request);

    @PreAuthorize("hasAnyRole('ADMIN')")
    void deleteProduct(Long id);

    List<ProductResponse> getAllProducts();

    List<ProductResponse> searchAndSortProducts(String search, String sortBy, String direction);

    ProductResponse getProductById(Long id);

    List<ProductResponse> getProductsByCategoryName(String categoryName);
}

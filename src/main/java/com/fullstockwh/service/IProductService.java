package com.fullstockwh.service;

import com.fullstockwh.dto.request.ProductRequest;
import com.fullstockwh.dto.response.ProductResponse;
import com.fullstockwh.enums.Color;
import com.fullstockwh.enums.Size;

import java.util.List;

public interface IProductService
{
    ProductResponse createProduct(ProductRequest request);
    ProductResponse getProductById(Long id);
    List<ProductResponse> getAllProducts();
    List<ProductResponse> searchProducts(String name);
    List<ProductResponse> filterProducts(Color color, Size size);
    void deleteProduct(Long id);
}

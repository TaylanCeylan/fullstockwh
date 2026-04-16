package com.fullstockwh.service.impl;

import com.fullstockwh.dto.request.ProductCreateRequest;
import com.fullstockwh.dto.response.ProductResponse;
import com.fullstockwh.entity.Category;
import com.fullstockwh.entity.Product;
import com.fullstockwh.repository.CategoryRepository;
import com.fullstockwh.repository.ProductRepository;
import com.fullstockwh.service.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService
{
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public ProductResponse createProduct(ProductCreateRequest request) {
        // 1. Find Category
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found! ID: " + request.getCategoryId()));

        String finalSku = (request.getSku() == null || request.getSku().isBlank())
                ? generateAutoSku(request.getName())
                : request.getSku();

        // 2. Make entity from Request
        Product product = Product.builder()
                .name(request.getName())
                .price(request.getPrice())
                .sku(finalSku)
                .stockQuantity(request.getStockQuantity())
                .category(category)
                .build();

        // 3. Add to Database
        productRepository.save(product);

        // 4. Convert saved entity to responseDTO
        return mapToResponse(product);
    }

    private String generateAutoSku(String productName) { // sku generator
        String prefix = productName.length() >= 3
                ? productName.substring(0, 3).toUpperCase()
                : "PRD";
        int randomNum = (int) (Math.random() * 9000) + 1000;
        return prefix + "-" + randomNum;
    }

    @Override
    public ProductResponse updateStock(Long id, Integer quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found! ID: " + id));

        product.setStockQuantity(quantity);
        productRepository.save(product);

        return mapToResponse(product);
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToResponse) // convert every product to response
                .collect(Collectors.toList());
    }

    // entity to dto mapping
    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .sku(product.getSku())
                .stockQuantity(product.getStockQuantity())
                .categoryName(product.getCategory().getName())
                .build();
    }
}

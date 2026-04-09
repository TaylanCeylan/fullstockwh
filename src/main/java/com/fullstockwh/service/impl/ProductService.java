package com.fullstockwh.service.impl;

import com.fullstockwh.dto.request.ProductRequest;
import com.fullstockwh.dto.response.ProductResponse;
import com.fullstockwh.entity.Category;
import com.fullstockwh.entity.Product;
import com.fullstockwh.enums.Color;
import com.fullstockwh.enums.Size;
import com.fullstockwh.exception.ResourceNotFoundException;
import com.fullstockwh.repository.ICategoryRepository;
import com.fullstockwh.repository.IProductRepository;
import com.fullstockwh.service.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService
{
    private final IProductRepository productRepository;
    private final ICategoryRepository categoryRepository;

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        // 1. Kategori kontrolü
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Kategori bulunamadı: " + request.getCategoryId()));

        // 2. Mapping: Request -> Entity (Builder kullanarak kankanın stiline uyduk)
        Product product = Product.builder()
                .name(request.getName())
                .brand(request.getBrand())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .weight(request.getWeight())
                .width(request.getWidth())
                .height(request.getHeight())
                .length(request.getLength())
                .color(request.getColor())
                .size(request.getSize())
                .category(category)
                .sku("FS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .build();

        Product savedProduct = productRepository.save(product);
        return mapToResponse(savedProduct);
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ürün bulunamadı: " + id));
        return mapToResponse(product);
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> searchProducts(String name) {
        return productRepository.findByNameContainingIgnoreCase(name).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> filterProducts(Color color, Size size) {
        return productRepository.findByColorAndSize(color, size).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public void deleteProduct(Long id) {
        if(!productRepository.existsById(id)) throw new ResourceNotFoundException("Ürün bulunamadı");
        productRepository.deleteById(id);
    }

    // Entity -> Response DTO Mapping
    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .sku(product.getSku())
                .brand(product.getBrand())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .categoryName(product.getCategory().getName())
                .color(product.getColor())
                .size(product.getSize())
                .build();
    }
}

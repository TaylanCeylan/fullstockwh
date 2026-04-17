package com.fullstockwh.service.impl;

import com.fullstockwh.dto.request.ProductCreateRequest;
import com.fullstockwh.dto.request.ProductUpdateRequest;
import com.fullstockwh.dto.request.VariantCreateRequest;
import com.fullstockwh.dto.request.VariantUpdateRequest;
import com.fullstockwh.dto.response.ProductResponse;
import com.fullstockwh.dto.response.VariantResponse;
import com.fullstockwh.entity.Category;
import com.fullstockwh.entity.Product;
import com.fullstockwh.entity.ProductVariant;
import com.fullstockwh.exception.ResourceNotFoundException;
import com.fullstockwh.repository.CategoryRepository;
import com.fullstockwh.repository.ProductRepository;
import com.fullstockwh.repository.ProductVariantRepository;
import com.fullstockwh.service.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService
{
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductVariantRepository variantRepository;

    @Transactional
    @Override
    public ProductResponse createProduct(ProductCreateRequest request) {
        //Find Category
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found! ID: " + request.getCategoryId()));

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .category(category)
                .variants(new ArrayList<>())
                .build();


        if (request.getVariants() != null) {
            for (VariantCreateRequest vReq : request.getVariants()) {
                //Validate based on category rules
                validateAttributes(category, vReq.getAttributes());

                //Generate Smart SKU
                String finalSku = (vReq.getSku() == null || vReq.getSku().isBlank())
                        ? generateSmartSku(product, vReq.getAttributes())
                        : vReq.getSku();

                ProductVariant variant = ProductVariant.builder()
                        .sku(finalSku)
                        .price(vReq.getPrice())
                        .stockQuantity(vReq.getStockQuantity())
                        .weight(vReq.getWeight()).width(vReq.getWidth())
                        .height(vReq.getHeight()).length(vReq.getLength())
                        .attributes(vReq.getAttributes())
                        .product(product)
                        .build();

                product.getVariants().add(variant);
            }
        }

        return mapToResponse(productRepository.save(product));
    }

    @Transactional
    @Override
    public ProductResponse updateProduct(Long id, ProductUpdateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found! ID: " + id));

        //Category Update Logic
        if (!product.getCategory().getId().equals(request.getCategoryId())) {
            Category newCategory = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("New category not found!"));
            product.setCategory(newCategory);
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setImageUrl(request.getImageUrl());

        //Complex Variant Update (ID Based)
        if (request.getVariants() != null) {
            //keep existing variants in a list (Orphan removal manual)
            List<ProductVariant> currentVariants = product.getVariants();

            for (VariantUpdateRequest vUpdateReq : request.getVariants()) {
                if (vUpdateReq.getId() != null) {
                    // SCENARIO A: Update existing variant
                    ProductVariant existingVariant = currentVariants.stream()
                            .filter(v -> v.getId().equals(vUpdateReq.getId()))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Variant not found for ID: " + vUpdateReq.getId()));

                    validateAttributes(product.getCategory(), vUpdateReq.getAttributes());
                    updateVariantFields(existingVariant, vUpdateReq);
                } else {
                    // SCENARIO B: Add new variant ( Id = Null)
                    validateAttributes(product.getCategory(), vUpdateReq.getAttributes());

                    ProductVariant newVariant = ProductVariant.builder()
                            .sku(vUpdateReq.getSku() == null ? generateSmartSku(product, vUpdateReq.getAttributes()) : vUpdateReq.getSku())
                            .price(vUpdateReq.getPrice())
                            .stockQuantity(vUpdateReq.getStockQuantity())
                            .attributes(vUpdateReq.getAttributes())
                            .product(product)
                            .build();
                    product.getVariants().add(newVariant);
                }
            }
        }

        return mapToResponse(productRepository.save(product));
    }

    @Transactional
    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found! ID: " + id);
        }
        // is_deleted = true with @SoftDelete
        productRepository.deleteById(id);
    }

    private String generateSmartSku(Product product, java.util.Map<String, String> attributes) {
        String genderPart = product.getCategory().getTargetGender().name().substring(0, 1);
        String catPart = (product.getCategory().getCode() != null) ? product.getCategory().getCode() : product.getCategory().getName().substring(0, 3).toUpperCase();

        String initials = Arrays.stream(product.getName().split("\\s+"))
                .map(word -> word.substring(0, 1).toUpperCase())
                .limit(3)
                .collect(Collectors.joining());

        String sizePart = (attributes != null && attributes.containsKey("Size")) ? attributes.get("Size").toUpperCase() : "XX";
        int random = (int) (Math.random() * 900) + 100;

        return String.format("%s-%s-%s%d-%s", genderPart, catPart, initials, random, sizePart);
    }

    @Transactional
    @Override
    public ProductResponse updateVariantStock(Long variantId, Integer quantity) {
        ProductVariant productVariant= variantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Variant not found! ID: " + variantId));

        productVariant.setStockQuantity(quantity);
        variantRepository.save(productVariant);

        return mapToResponse(productVariant.getProduct());
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToResponse) // convert every product to response
                .collect(Collectors.toList());
    }

    private void validateAttributes(Category category, java.util.Map<String, String> attributes) {
        List<String> requiredRules = category.getRequiredAttributes();
        if (requiredRules != null) {
            for (String rule : requiredRules) {
                if (attributes == null || !attributes.containsKey(rule) || attributes.get(rule).isBlank()) {
                    throw new IllegalArgumentException("The field '" + rule + "' is mandatory for " + category.getName());
                }
            }
        }
    }

    @Override
    public List<ProductResponse> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllProducts();
        }

        return productRepository.searchProducts(keyword).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private void updateVariantFields(ProductVariant existing, VariantUpdateRequest req) {
        existing.setPrice(req.getPrice());
        existing.setStockQuantity(req.getStockQuantity());
        existing.setAttributes(req.getAttributes());
        existing.setWeight(req.getWeight());
        existing.setHeight(req.getHeight());
        existing.setWidth(req.getWidth());
        existing.setLength(req.getLength());
    }

    // entity to dto mapping
    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .imageUrl(product.getImageUrl())
                .categoryName(product.getCategory().getName())
                .targetGender(product.getCategory().getTargetGender())
                .variants(product.getVariants().stream().map(v -> VariantResponse.builder()
                        .id(v.getId()).sku(v.getSku()).price(v.getPrice())
                        .stockQuantity(v.getStockQuantity()).attributes(v.getAttributes())
                        .weight(v.getWeight()).width(v.getWidth()).height(v.getHeight()).length(v.getLength())
                        .build()).collect(Collectors.toList()))
                .build();
    }
}

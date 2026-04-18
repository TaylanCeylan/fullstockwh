package com.fullstockwh.product.product_variant;

import com.fullstockwh.product.product_variant.dto.VariantCreateRequest;
import com.fullstockwh.product.product_variant.dto.VariantUpdateRequest;
import com.fullstockwh.product.product_variant.dto.VariantResponse;
import com.fullstockwh.product.Product;
import com.fullstockwh.product.product_variant.enums.Color;
import com.fullstockwh.product.product_variant.enums.Size;
import com.fullstockwh.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class VariantServiceImpl implements VariantService
{
    private final VariantRepository variantRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public void addVariantToProduct(VariantCreateRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found. Variant cannot be added!"));

        // Generating SKU
        String generatedSku = generateSmartSku(product.getName(), request.getColor(), request.getSize());

        ProductVariant variant = ProductVariant.builder()
                .sku(generatedSku)
                .color(request.getColor())
                .size(request.getSize())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .weight(request.getWeight())
                .width(request.getWidth())
                .height(request.getHeight())
                .length(request.getLength())
                .product(product)
                .build();

        variantRepository.save(variant);
    }

    @Override
    @Transactional
    public void updateVariant(VariantUpdateRequest request, Long id) {
        ProductVariant variant = variantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Variant not found to be updated!"));

        variant.setPrice(request.getPrice());
        variant.setStockQuantity(request.getStockQuantity());
        variant.setWeight(request.getWeight());
        variant.setWidth(request.getWidth());
        variant.setHeight(request.getHeight());
        variant.setLength(request.getLength());

        variantRepository.save(variant);
    }

    @Override
    @Transactional
    public void deleteVariant(Long variantId) {
        if (!variantRepository.existsById(variantId)) {
            throw new RuntimeException("Variant not found to be deleted!");
        }
        variantRepository.deleteById(variantId);
    }

    // Generating smart SKU
    private String generateSmartSku(String name, Color color, Size size) {
        String prefix = (name != null && name.length() >= 3)
                ? name.substring(0, 3).toUpperCase()
                : "PRD";

        String colorPart = color.name().substring(0, 3);
        String sizePart = size.name();
        int randomNum = (int) (Math.random() * 9000) + 1000;

        return String.format("%s-%s-%s-%d", prefix, colorPart, sizePart, randomNum);
    }

    @Override
    public VariantResponse mapToVariantResponse(ProductVariant variant) {
        if (variant == null) return null;

        return VariantResponse.builder()
                .id(variant.getId())
                .sku(variant.getSku())
                .color(variant.getColor().name())
                .size(variant.getSize().name())
                .price(variant.getPrice())
                .stockQuantity(variant.getStockQuantity())
                .weight(variant.getWeight())
                .width(variant.getWidth())
                .height(variant.getHeight())
                .length(variant.getLength())
                .build();
    }
}

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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        Optional<ProductVariant> existing = variantRepository
                .findByProductIdAndColorAndSize(request.getProductId(), request.getColor(), request.getSize());

        if (existing.isPresent()) {

            ProductVariant variant = existing.get();

            boolean existingHasWeight = variant.getUnitWeight() != null;
            boolean newHasWeight      = request.getUnitWeight() != null;

            if (existingHasWeight && newHasWeight
                    && !variant.getUnitWeight().equals(request.getUnitWeight())) {
                throw new RuntimeException(
                        "The unit weight for this variant is different from the previous entry. " +
                                "Please enter the correct unit weight."
                );
            }
            variant.setStockQuantity(variant.getStockQuantity() + request.getStockQuantity());

            if (request.getUnitWeight() != null) {
                variant.setUnitWeight(request.getUnitWeight());
            }
            variantRepository.save(variant);

        } else {

        // Generating SKU
        String generatedSku = generateSmartSku(product.getName(), request.getColor(), request.getSize());

        ProductVariant variant = ProductVariant.builder()
                .sku(generatedSku)
                .color(request.getColor())
                .size(request.getSize())
                .stockQuantity(request.getStockQuantity())
                .unitWeight(request.getUnitWeight())
                .product(product)
                .build();

        variantRepository.save(variant);
        }
    }
    @Override
    @Transactional
    public void updateVariant(VariantUpdateRequest request, Long id) {
        ProductVariant variant = variantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Variant not found to be updated!"));

        variant.setStockQuantity(request.getStockQuantity());
        variant.setUnitWeight(request.getUnitWeight());

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
                .color(variant.getColor() != null ? variant.getColor().name() : null)
                .size(variant.getSize() != null ? variant.getSize().name() : null)
                .stockQuantity(variant.getStockQuantity())
                .unitWeight(variant.getUnitWeight())
                .stockStatus(resolveStockStatus(variant.getStockQuantity()))
                .build();
    }
    @Override
    public List<VariantResponse> getVariantsByProductId(Long productId) {
        return variantRepository.findByProductId(productId)
                .stream()
                .map(this::mapToVariantResponse)
                .collect(Collectors.toList());
    }

    private String resolveStockStatus(int quantity) {
        if (quantity > 10)  return "IN_STOCK";
        if (quantity > 0)   return "LOW_STOCK";
        return "OUT_OF_STOCK";
    }
}

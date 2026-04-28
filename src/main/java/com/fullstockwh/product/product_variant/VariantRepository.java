package com.fullstockwh.product.product_variant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import com.fullstockwh.product.product_variant.enums.Color;
import com.fullstockwh.product.product_variant.enums.Size;

@Repository
public interface VariantRepository extends JpaRepository<ProductVariant, Long>
{
    List<ProductVariant> findByStockQuantityLessThan(Integer quantity);

    List<ProductVariant> findByProductId(Long productId);

    Optional<ProductVariant> findByProductIdAndColorAndSize(Long productId, Color color, Size size);
}

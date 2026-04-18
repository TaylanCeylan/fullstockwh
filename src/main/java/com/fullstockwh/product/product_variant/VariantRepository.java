package com.fullstockwh.product.product_variant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VariantRepository extends JpaRepository<ProductVariant, Long>
{
    List<ProductVariant> findByStockQuantityLessThan(Integer quantity);

}

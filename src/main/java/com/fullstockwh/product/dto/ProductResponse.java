package com.fullstockwh.product.dto;

import com.fullstockwh.product.product_variant.dto.VariantResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse
{
    private Long id;
    private String name;
    private BigDecimal price;
    private String description;
    private String categoryName;
    private String gender;
    private List<VariantResponse> variants;
}

package com.fullstockwh.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VariantResponse
{
    private Long id;

    private String sku;

    private Double price;

    private Integer stockQuantity;

    //logistics attributes
    private Double weight;
    private Double width;
    private Double height;
    private Double length;

    private Map<String, String> attributes; //Send JSONB box to frontend with map
}

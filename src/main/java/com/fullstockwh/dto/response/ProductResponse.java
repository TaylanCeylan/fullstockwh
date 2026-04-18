package com.fullstockwh.dto.response;

import com.fullstockwh.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse
{
    private Long id;
    private String name;
    private String description;
    private String categoryName;
    private String gender;
    private List<VariantResponse> variants;
}

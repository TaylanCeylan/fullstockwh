package com.fullstockwh.dto.response;

import com.fullstockwh.enums.TargetGender;
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

    private String imageUrl;

    private TargetGender targetGender;

    private String categoryName;

    private List<VariantResponse> variants; //return variant list to frontend
}

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
public class CategoryResponse
{
    private Long id;
    private String name;
    private String code;
    private TargetGender targetGender;
    private List<String> requiredAttributes;
}

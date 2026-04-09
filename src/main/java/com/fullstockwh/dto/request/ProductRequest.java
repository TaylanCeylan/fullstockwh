package com.fullstockwh.dto.request;

import com.fullstockwh.enums.Color;
import com.fullstockwh.enums.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductRequest
{
    private String name;
    private String brand;
    private String description;
    private Double price;
    private Integer stockQuantity;
    private Long categoryId;
    private Color color;
    private Size size;
    // Lojistik detaylar
    private Double weight;
    private Double width;
    private Double height;
    private Double length;
}

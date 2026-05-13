package com.fullstockwh.cart.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CartItemResponse
{
    private Long cartItemId;
    private Long variantId;
    private String productName;
    private String color;
    private String size;
    private int quantity;
    private int stockQuantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}

package com.fullstockwh.order.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class OrderItemResponse
{
    private Long id;
    private String productName;
    private String color;
    private String size;
    private int quantity;
    private BigDecimal priceAtPurchase;
    private BigDecimal subtotal;
}

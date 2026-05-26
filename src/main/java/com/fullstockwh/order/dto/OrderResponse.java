package com.fullstockwh.order.dto;

import com.fullstockwh.order.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse
{
    private Long id;
    private BigDecimal totalPrice;
    private OrderStatus status;
    private String shippingAddress;
    private LocalDateTime orderDate;
    private LocalDateTime cancelledAt;
    private List<OrderItemResponse> items;
}

package com.fullstockwh.order;

import com.fullstockwh.order.dto.OrderCreateRequest;
import com.fullstockwh.order.dto.OrderResponse;
import com.fullstockwh.user.UserEntity;

import java.util.List;

public interface OrderService
{
    OrderResponse placeOrder(UserEntity user, OrderCreateRequest request);
    List<OrderResponse> getOrdersByUser (UserEntity user);
    void cancelOrder(Long orderId, UserEntity user);
    void adminCancelOrder(Long orderId);
}

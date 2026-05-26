package com.fullstockwh.order;

import com.fullstockwh.cart.Cart;
import com.fullstockwh.cart.CartRepository;
import com.fullstockwh.cart.cart_item.CartItem;
import com.fullstockwh.order.dto.OrderCreateRequest;
import com.fullstockwh.order.dto.OrderItemResponse;
import com.fullstockwh.order.dto.OrderResponse;
import com.fullstockwh.order.enums.OrderStatus;
import com.fullstockwh.order.order_item.OrderItem;
import com.fullstockwh.product.product_variant.ProductVariant;
import com.fullstockwh.product.product_variant.VariantRepository;
import com.fullstockwh.user.UserEntity;
import com.fullstockwh.user.address.Address;
import com.fullstockwh.user.address.AddressRepository;
import com.fullstockwh.common.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService
{
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final VariantRepository variantRepository;
    private final AddressRepository addressRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public OrderResponse placeOrder(UserEntity user, OrderCreateRequest request) {

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getCartItems().isEmpty())
            throw new RuntimeException("Cart is empty");

        for (CartItem cartItem : cart.getCartItems()) {
            ProductVariant variant = cartItem.getProductVariant();
            if (variant.getStockQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException(
                        "Insufficient stock for: "
                                + variant.getProduct().getName()
                                + " (" + variant.getColor() + " / " + variant.getSize() + ")"
                );
            }
        }

        Address address = addressRepository
                .findByIdAndUser(request.getAddressId(), user)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        Order order = Order.builder()
                .user (user)
                .shippingAddress(address)
                .status (OrderStatus.SUCCESS)
                .totalPrice (BigDecimal.valueOf(cart.getTotalAmount()))
                .items (new ArrayList<>())
                .build();

        for (CartItem cartItem : cart.getCartItems()) {
            ProductVariant variant = cartItem.getProductVariant();

            OrderItem orderItem = OrderItem.builder()
                    .order (order)
                    .productVariant (variant)
                    .quantity (cartItem.getQuantity())
                    .priceAtPurchase (variant.getProduct().getPrice())
                    .build();

            order.getItems().add(orderItem);

            variant.setStockQuantity(
                    variant.getStockQuantity() - cartItem.getQuantity()
            );
            variantRepository.save(variant);
        }

        orderRepository.save(order);

        cart.getCartItems().clear();
        cartRepository.save(cart);

        return mapToResponse(order);
    }

    @Override
    public List<OrderResponse> getOrdersByUser(UserEntity user) {
        return orderRepository.findByUserWithItems(user)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId, UserEntity user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(user.getId()))
            throw new RuntimeException("Unauthorized");

        if (order.getStatus() != OrderStatus.SUCCESS)
            throw new RuntimeException("Only confirmed orders can be cancelled");

        if (order.getOrderDate().isBefore(LocalDateTime.now().minusMinutes(30)))
            throw new RuntimeException("Cancellation period has expired (30 minutes)");

        restoreStockAndCancel(order);
    }

    @Override
    @Transactional
    public void adminCancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() == OrderStatus.DELIVERED ||
                order.getStatus() == OrderStatus.CANCELLED)
            throw new RuntimeException("This order cannot be cancelled");

        restoreStockAndCancel(order);
    }

    private void restoreStockAndCancel(Order order) {
        for (OrderItem item : order.getItems()) {
            ProductVariant variant = item.getProductVariant();
            variant.setStockQuantity(variant.getStockQuantity() + item.getQuantity());
            variantRepository.save(variant);
        }
        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelledAt(LocalDateTime.now());
        orderRepository.save(order);
        emailService.sendOrderCancellationEmail(
                order.getUser().getUsername(), order.getId());
    }

    @Override
    @Transactional
    public void shipOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (order.getStatus() != OrderStatus.SUCCESS)
            throw new RuntimeException("Only confirmed orders can be shipped");
        order.setStatus(OrderStatus.SHIPPED);
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void deliverOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (order.getStatus() != OrderStatus.SHIPPED)
            throw new RuntimeException("Only shipped orders can be marked as delivered");
        order.setStatus(OrderStatus.DELIVERED);
        orderRepository.save(order);
    }

    private OrderResponse mapToResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .id (item.getId())
                        .productName (item.getProductVariant().getProduct().getName())
                        .color (item.getProductVariant().getColor() != null
                                ? item.getProductVariant().getColor().name() : null)
                        .size (item.getProductVariant().getSize() != null
                                ? item.getProductVariant().getSize().name() : null)
                        .quantity (item.getQuantity())
                        .priceAtPurchase(item.getPriceAtPurchase())
                        .subtotal (item.getPriceAtPurchase()
                                .multiply(BigDecimal.valueOf(item.getQuantity())))
                        .build())
                .collect(Collectors.toList());

        String shippingAddr = order.getShippingAddress() != null
                ? order.getShippingAddress().getFullAddress()
                + ", " + order.getShippingAddress().getCity()
                : "—";

        return OrderResponse.builder()
                .id (order.getId())
                .totalPrice (order.getTotalPrice())
                .status (order.getStatus())
                .orderDate (order.getOrderDate())
                .shippingAddress(shippingAddr)
                .items (itemResponses)
                .cancelledAt (order.getCancelledAt())
                .build();
    }
}

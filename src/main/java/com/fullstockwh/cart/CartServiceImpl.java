package com.fullstockwh.cart;

import com.fullstockwh.cart.cart_item.CartItem;
import com.fullstockwh.cart.cart_item.CartItemRepository;
import com.fullstockwh.cart.dto.*;
import com.fullstockwh.product.product_variant.ProductVariant;
import com.fullstockwh.product.product_variant.VariantRepository;
import com.fullstockwh.user.UserEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final VariantRepository variantRepository;

    @Override
    @Transactional
    public void addToCart(UserEntity currentUser, AddToCartRequest request) {
        ProductVariant variant = variantRepository.findById(request.getVariantId())
                .orElseThrow(() -> new RuntimeException("Variant not found"));

        if (variant.getStockQuantity() < request.getQuantity()) {
            throw new RuntimeException("Not enough stock quantity");
        }

        Cart cart = cartRepository.findByUser(currentUser)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(currentUser);
                    return cartRepository.save(newCart);
                });

        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProductVariant().getId().equals(variant.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + request.getQuantity();

            if (newQuantity > variant.getStockQuantity()) {
                throw new RuntimeException("Not enough stock quantity");
            }

            item.setQuantity(newQuantity);
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProductVariant(variant);
            newItem.setQuantity(request.getQuantity());
            cart.getCartItems().add(newItem);
            cartItemRepository.save(newItem);
        }
    }

    @Override
    @Transactional
    public void removeFromCart(UserEntity currentUser, RemoveFromCartRequest request) {
        Cart cart = cartRepository.findByUser(currentUser)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.getCartItems().stream()
                .filter(i -> i.getProductVariant().getId().equals(request.getVariantId()))
                .findFirst()
                .ifPresent(item -> cart.getCartItems().remove(item));

        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public CartResponse updateQuantity(UserEntity currentUser, UpdateCartRequest request) {
        ProductVariant variant = variantRepository.findById(request.getVariantId())
                .orElseThrow(() -> new RuntimeException("Variant not found"));

        if (request.getQuantity() < 1) throw new RuntimeException("Quantity must be at least 1");
        if (request.getQuantity() > variant.getStockQuantity())
            throw new RuntimeException("Not enough stock");

        Cart cart = cartRepository.findByUser(currentUser)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.getCartItems().stream()
                .filter(i -> i.getProductVariant().getId().equals(request.getVariantId()))
                .findFirst()
                .ifPresent(item -> {
                    item.setQuantity(request.getQuantity());
                    cartItemRepository.save(item);
                });
        return mapToResponse(cart);
    }

    @Override
    public CartResponse getCart(UserEntity user)
    {
        Cart cart = cartRepository.findByUser(user).orElse(new Cart());

        return mapToResponse(cart);
    }

    private CartResponse mapToResponse(Cart cart)
    {
        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            return CartResponse.builder()
                    .cartId(cart.getId())
                    .items(Collections.emptyList())
                    .totalAmount(BigDecimal.ZERO)
                    .totalItems(0)
                    .build();
        }

        List<CartItemResponse> items = cart.getCartItems().stream()
                .map(item -> {
                    BigDecimal unitPrice = item.getProductVariant().getProduct().getPrice();
                    BigDecimal subtotal  = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
                    return CartItemResponse.builder()
                            .cartItemId (item.getId())
                            .variantId (item.getProductVariant().getId())
                            .productName (item.getProductVariant().getProduct().getName())
                            .color (item.getProductVariant().getColor() != null
                                    ? item.getProductVariant().getColor().name() : null)
                            .size (item.getProductVariant().getSize() != null
                                    ? item.getProductVariant().getSize().name() : null)
                            .quantity (item.getQuantity())
                            .stockQuantity(item.getProductVariant().getStockQuantity())
                            .unitPrice (unitPrice)
                            .subtotal (subtotal)
                            .build();
                })
                .collect(Collectors.toList());

        return CartResponse.builder()
                .cartId (cart.getId())
                .items (items)
                .totalAmount(BigDecimal.valueOf(cart.getTotalAmount()))
                .totalItems (cart.getCartItems().size())
                .build();
    }
}

package com.fullstockwh.cart;

import com.fullstockwh.cart.dto.AddToCartRequest;
import com.fullstockwh.cart.dto.CartResponse;
import com.fullstockwh.cart.dto.RemoveFromCartRequest;
import com.fullstockwh.cart.dto.UpdateCartRequest;
import com.fullstockwh.user.UserEntity;

public interface CartService
{
    void addToCart(UserEntity currentUser, AddToCartRequest request);
    void removeFromCart(UserEntity currentUser, RemoveFromCartRequest request);
    CartResponse updateQuantity(UserEntity currentUser, UpdateCartRequest request);
    CartResponse getCart(UserEntity currentUser);
}

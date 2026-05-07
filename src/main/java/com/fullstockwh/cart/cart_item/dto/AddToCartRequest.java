package com.fullstockwh.cart.cart_item.dto;

import lombok.Data;

@Data
public class AddToCartRequest
{
    private long variantId;
    private int quantity;
}

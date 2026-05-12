package com.fullstockwh.cart.dto;

import lombok.Data;

@Data
public class AddToCartRequest
{
    private long variantId;
    private int quantity;
}

package com.fullstockwh.cart.dto;

import lombok.Data;

@Data
public class UpdateCartRequest
{
    private Long variantId;
    private int  quantity;
}

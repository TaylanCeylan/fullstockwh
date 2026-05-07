package com.fullstockwh.cart.cart_item;

import com.fullstockwh.cart.Cart;
import com.fullstockwh.product.product_variant.ProductVariant;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class CartItem
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id")
    private ProductVariant productVariant;

    private int quantity;
}

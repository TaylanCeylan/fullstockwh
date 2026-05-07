package com.fullstockwh.cart;

import com.fullstockwh.cart.cart_item.CartItem;
import com.fullstockwh.user.UserEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Cart
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    public Double getTotalAmount() {
        if (this.cartItems == null || this.cartItems.isEmpty())
        {
            return 0.0;
        }

        Double total = 0.0;

        for (CartItem item : this.cartItems)
        {
            Double itemPrice = item.getProductVariant().getProduct().getPrice().doubleValue();

            int quantity = item.getQuantity();
            
            total += (itemPrice * quantity);
        }

        return total;
    }
}

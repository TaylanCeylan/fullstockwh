package com.fullstockwh.cart;

import com.fullstockwh.cart.cart_item.CartItem;
import com.fullstockwh.cart.cart_item.CartItemRepository;
import com.fullstockwh.product.product_variant.ProductVariant;
import com.fullstockwh.product.product_variant.VariantRepository;
import com.fullstockwh.user.UserEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final VariantRepository variantRepository;

    @Transactional

    public void addToCart(UserEntity currentUser, Long variantId, int quantity)
    {
        ProductVariant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Variant not found"));

        if (variant.getStockQuantity() < quantity)
        {
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

        if (existingItem.isPresent())
        {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;

            if(newQuantity > variant.getStockQuantity())
            {
                throw new RuntimeException("Not enough stock quantity");
            }

            item.setQuantity(newQuantity);
            cartItemRepository.save(item);
        }
        else
        {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProductVariant(variant);
            newItem.setQuantity(quantity);

            cart.getCartItems().add(newItem);
            cartItemRepository.save(newItem);
        }
    }

    @Transactional
    public void removeFromCart(UserEntity currentUser, Long variantId)
    {
        Cart cart = cartRepository.findByUser(currentUser)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        Optional<CartItem> itemOpt = cart.getCartItems().stream()
                .filter(item -> item.getProductVariant().getId().equals(variantId))
                .findFirst();

        if (itemOpt.isPresent())
        {
            CartItem item = itemOpt.get();

            if (item.getQuantity() > 1)
            {
                item.setQuantity(item.getQuantity() - 1);
            }
            else
            {
                cart.getCartItems().remove(item);
            }

            cartRepository.save(cart);
        }
    }
}

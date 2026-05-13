package com.fullstockwh.cart;

import com.fullstockwh.cart.dto.AddToCartRequest;
import com.fullstockwh.cart.dto.CartResponse;
import com.fullstockwh.cart.dto.RemoveFromCartRequest;
import com.fullstockwh.cart.dto.UpdateCartRequest;
import com.fullstockwh.user.UserEntity;
import com.fullstockwh.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class CartController
{
    private final CartService cartService;
    private final UserService userService;

    private UserEntity getUser(UserDetails userDetails) {
        return userService.findByEmail(userDetails.getUsername());
    }

    @GetMapping("/cart")
    public String showCartPage(@AuthenticationPrincipal UserDetails userDetails, Model model)
    {
        if (userDetails == null)
        {
            return "redirect:/login";
        }

        CartResponse cart = cartService.getCart(getUser(userDetails));
        model.addAttribute("cart", cart);
        return "cart";
    }

    @PostMapping("/cart/add")
    public String addToCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute AddToCartRequest request)
    {
        if (userDetails == null)
        {
            return "redirect:/login";
        }

        cartService.addToCart(getUser(userDetails), request);

        return "redirect:/cart";
    }

    @PostMapping("/cart/remove")
    public String removeFromCart(@AuthenticationPrincipal UserDetails userDetails,
                                 @ModelAttribute RemoveFromCartRequest request)
    {
        if (userDetails == null)
        {
            return "redirect:/login";
        }

        cartService.removeFromCart(getUser(userDetails),  request);

        return "redirect:/cart";
    }

    @PostMapping("/cart/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateQuantity(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UpdateCartRequest request) {
        if (userDetails == null) return ResponseEntity.status(401).build();

        CartResponse cart = cartService.updateQuantity(getUser(userDetails), request);

        double itemSubtotal = cart.getItems().stream()
                .filter(i -> i.getVariantId().equals(request.getVariantId()))
                .mapToDouble(i -> i.getSubtotal().doubleValue())
                .findFirst().orElse(0);

        return ResponseEntity.ok(Map.of(
                "itemSubtotal", itemSubtotal,
                "cartTotal",    cart.getTotalAmount().doubleValue()
        ));
    }
}

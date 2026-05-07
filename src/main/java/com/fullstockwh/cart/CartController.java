package com.fullstockwh.cart;

import com.fullstockwh.user.UserEntity;
import com.fullstockwh.user.UserRepository;
import org.apache.catalina.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CartController
{
    private final CartServiceImpl cartService;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;

    public CartController(CartServiceImpl cartService, UserRepository userRepository, CartRepository cartRepository)
    {
        this.cartService = cartService;
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
    }

    @GetMapping("/cart")
    public String showCartPage(@AuthenticationPrincipal UserDetails userDetails, Model model)
    {
        if (userDetails == null)
        {
            return "redirect:/login";
        }

        UserEntity currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUser(currentUser).orElse(new Cart());

        model.addAttribute("cart", cart);

        return "cart";
    }

    @PostMapping("/cart/add")
    public String addToCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("variantId") Long variantId,
            @RequestParam("quantity") int quantity) {

        UserEntity currentUser = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));

        cartService.addToCart(currentUser, variantId, quantity);

        return "redirect:/cart";
    }

    @PostMapping("/cart/remove")
    public String removeFromCart(@AuthenticationPrincipal UserDetails userDetails,
                                 @RequestParam("variantId") Long variantId) {

        if (userDetails == null)
        {
            return "redirect:/login";
        }

        UserEntity currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        cartService.removeFromCart(currentUser, variantId);

        return "redirect:/cart";
    }
}

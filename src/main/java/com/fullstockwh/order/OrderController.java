package com.fullstockwh.order;

import com.fullstockwh.order.dto.OrderResponse;
import com.fullstockwh.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserService  userService;

    @GetMapping("/orders")
    public String myOrders(@AuthenticationPrincipal UserDetails userDetails,
                           Model model) {
        if (userDetails == null) return "redirect:/login";

        List<OrderResponse> orders = orderService.getOrdersByUser(
                userService.findByEmail(userDetails.getUsername()));
        model.addAttribute("orders", orders);
        return "orders";
    }

    @PostMapping("/orders/{id}/cancel")
    public String cancelOrder(@PathVariable Long id,
                              @AuthenticationPrincipal UserDetails userDetails,
                              RedirectAttributes redirectAttributes) {
        if (userDetails == null) return "redirect:/login";
        try {
            orderService.cancelOrder(id,
                    userService.findByEmail(userDetails.getUsername()));
            redirectAttributes.addFlashAttribute("success",
                    "Order #" + id + " has been cancelled.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/orders";
    }
}

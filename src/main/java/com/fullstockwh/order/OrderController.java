package com.fullstockwh.order;

import com.fullstockwh.order.dto.OrderResponse;
import com.fullstockwh.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
}

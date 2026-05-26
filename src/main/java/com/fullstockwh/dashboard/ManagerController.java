package com.fullstockwh.dashboard;

import com.fullstockwh.order.Order;
import com.fullstockwh.order.OrderRepository;
import com.fullstockwh.order.OrderService;
import com.fullstockwh.order.enums.OrderStatus;
import com.fullstockwh.product.ProductService;
import com.fullstockwh.product.dto.ProductResponse;
import com.fullstockwh.product.product_variant.VariantRepository;
import com.fullstockwh.shipment.ShipmentRepository;
import com.fullstockwh.shipment.enums.ShipmentStatus;
import com.fullstockwh.shipment.ShipmentService;
import com.fullstockwh.shipment.dto.ShipOrderRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/manager")
@RequiredArgsConstructor
public class ManagerController
{
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final ProductService productService;
    private final VariantRepository variantRepository;
    private final ShipmentService shipmentService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Order> allOrders = orderRepository.findRecentOrdersWithDetails(PageRequest.of(0, 200));

        long pendingOrders = allOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.SUCCESS)
                .count();
        long shippedOrders = allOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.SHIPPED)
                .count();

        int lowStockCount = variantRepository.findByStockQuantityLessThan(5).size();

        List<ProductResponse> products = productService.getAllProducts();

        model.addAttribute("pendingOrders", pendingOrders);
        model.addAttribute("shippedOrders", shippedOrders);
        model.addAttribute("lowStockCount", lowStockCount);
        model.addAttribute("products", products);
        model.addAttribute("recentOrders", allOrders.stream().limit(10).collect(Collectors.toList()));
        model.addAttribute("activePage", "dashboard");
        return "manager/dashboard";
    }

    @GetMapping("/orders")
    public String orders(
            @RequestParam(required = false, defaultValue = "") String status,
            Model model) {
        List<Order> allOrders;
        if (status != null && !status.isBlank()) {
            OrderStatus orderStatus = OrderStatus.valueOf(status);
            allOrders = orderRepository.findRecentOrdersWithDetails(PageRequest.of(0, 200))
                    .stream()
                    .filter(o -> o.getStatus() == orderStatus)
                    .collect(Collectors.toList());
        } else {
            allOrders = orderRepository.findRecentOrdersWithDetails(PageRequest.of(0, 200));
        }
        model.addAttribute("allOrders", allOrders);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("activePage", "orders");
        return "manager/orders";
    }

    @GetMapping("/orders/{id}/ship")
    public String showShipForm(@PathVariable Long id, Model model) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        model.addAttribute("orderId", id);

        if (order.getShippingAddress() != null) {
            model.addAttribute("destLat", order.getShippingAddress().getLatitude());
            model.addAttribute("destLon", order.getShippingAddress().getLongitude());
            model.addAttribute("destCity", order.getShippingAddress().getCity());
            model.addAttribute("destDistrict", order.getShippingAddress().getDistrict());
        }
        return "manager/ship-order";
    }

    @PostMapping("/orders/{id}/ship")
    public String shipOrder(@PathVariable Long id,
                            @RequestParam(defaultValue = "Fullstockwh Cargo") String carrierName,
                            RedirectAttributes redirectAttributes) {
        try {
            ShipOrderRequest request = new ShipOrderRequest();
            request.setOrderId(id);
            request.setCarrierName(carrierName);
            shipmentService.shipOrder(request);
            redirectAttributes.addFlashAttribute("success", "Order #" + id + " marked as shipped.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/manager/orders";
    }

    @PostMapping("/orders/{id}/deliver")
    public String deliverOrder(@PathVariable Long id,
                               RedirectAttributes redirectAttributes) {
        try {
            shipmentService.markDelivered(id);
            redirectAttributes.addFlashAttribute("success", "Order #" + id + " marked as delivered.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/manager/orders";
    }

    @GetMapping("/shipments")
    public String shipments(Model model) {
        model.addAttribute("shipments",
                shipmentService.getAllShipments());
        model.addAttribute("activePage", "shipments");
        return "manager/shipments";
    }
}

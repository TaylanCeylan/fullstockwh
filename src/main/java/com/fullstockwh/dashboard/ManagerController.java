package com.fullstockwh.dashboard;

import com.fullstockwh.common.StockThreshold;
import com.fullstockwh.order.Order;
import com.fullstockwh.order.OrderRepository;
import com.fullstockwh.order.OrderService;
import com.fullstockwh.order.enums.OrderStatus;
import com.fullstockwh.product.ProductService;
import com.fullstockwh.product.dto.ProductResponse;
import com.fullstockwh.product.product_variant.VariantRepository;
import com.fullstockwh.product.product_variant.VariantService;
import com.fullstockwh.shipment.ShipmentRepository;
import com.fullstockwh.shipment.enums.ShipmentStatus;
import com.fullstockwh.shipment.Shipment;
import com.fullstockwh.shipment.ShipmentService;
import com.fullstockwh.shipment.dto.ShipOrderRequest;
import com.fullstockwh.stock.StockRequestService;
import com.fullstockwh.stock.StockRequest;
import com.fullstockwh.user.UserEntity;
import com.fullstockwh.user.UserRepository;
import com.fullstockwh.user.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

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
    private final ShipmentRepository shipmentRepository;
    private final StockRequestService stockRequestService;
    private final UserService userService;
    private final VariantService variantService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Order> allOrders = orderRepository.findRecentOrdersWithDetails(PageRequest.of(0, 200));

        long pendingOrders = allOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.SUCCESS)
                .count();
        long shippedOrders = allOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.SHIPPED)
                .count();

        int lowStockCount = variantRepository.findByStockQuantityLessThan(StockThreshold.LOW_STOCK).size();

        long pendingStockRequests = stockRequestService.getPendingCount();
        List<Shipment> overdueShipments = shipmentRepository.findOverdueShipments(LocalDateTime.now());

        List<ProductResponse> products = productService.getAllProducts();

        model.addAttribute("pendingOrders", pendingOrders);
        model.addAttribute("shippedOrders", shippedOrders);
        model.addAttribute("lowStockCount", lowStockCount);
        model.addAttribute("products", products);
        List<Order> recentOrders = allOrders.stream().limit(10).collect(Collectors.toList());
        model.addAttribute("recentOrders", recentOrders);
        List<String> orderDates = recentOrders.stream()
                .map(o -> o.getOrderDate().toLocalDate().toString())
                .collect(Collectors.toList());
        model.addAttribute("orderDates", orderDates);
        model.addAttribute("pendingStockRequests", pendingStockRequests);
        model.addAttribute("overdueShipmentsCount", overdueShipments.size());
        model.addAttribute("activePage", "dashboard");
        return "manager/dashboard";
    }

    @GetMapping("/orders")
    public String orders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "date") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String direction,
            Model model) {

        List<Order> allOrders = orderRepository.findRecentOrdersWithDetails(PageRequest.of(0, 500));

        if (status != null && !status.isBlank()) {
            OrderStatus orderStatus = OrderStatus.valueOf(status);
            allOrders = allOrders.stream()
                    .filter(o -> o.getStatus() == orderStatus)
                    .collect(Collectors.toList());
        }

        if (search != null && !search.isBlank()) {
            String q = search.toLowerCase();
            allOrders = allOrders.stream()
                    .filter(o -> o.getUser() != null && (
                            o.getUser().getFirstName().toLowerCase().contains(q) ||
                                    o.getUser().getLastName().toLowerCase().contains(q) ||
                                    o.getUser().getUsername().toLowerCase().contains(q)))
                    .collect(Collectors.toList());
        }

        if ("total".equals(sortBy)) {
            allOrders.sort((a, b) -> "asc".equals(direction)
                    ? a.getTotalPrice().compareTo(b.getTotalPrice())
                    : b.getTotalPrice().compareTo(a.getTotalPrice()));
        } else {
            allOrders.sort((a, b) -> "asc".equals(direction)
                    ? a.getOrderDate().compareTo(b.getOrderDate())
                    : b.getOrderDate().compareTo(a.getOrderDate()));
        }

        model.addAttribute("allOrders", allOrders);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("search", search);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        model.addAttribute("activePage", "orders");
        return "manager/orders";
    }

    @GetMapping("/orders/{id}")
    public String orderDetail(@PathVariable Long id, Model model) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        model.addAttribute("order", order);
        model.addAttribute("shipment", order.getShipment());
        model.addAttribute("activePage", "orders");
        return "manager/order-detail";
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
    public String shipments(
            @RequestParam(required = false, defaultValue = "false") boolean overdueOnly,
            Model model) {
        List<com.fullstockwh.shipment.Shipment> shipments = overdueOnly
                ? shipmentRepository.findOverdueShipments(LocalDateTime.now())
                : shipmentRepository.findAll();

        java.util.Set<Long> overdueIds = shipmentRepository
                .findOverdueShipments(LocalDateTime.now())
                .stream()
                .map(com.fullstockwh.shipment.Shipment::getId)
                .collect(java.util.stream.Collectors.toSet());

        model.addAttribute("shipments", shipments);
        model.addAttribute("overdueIds", overdueIds);
        model.addAttribute("overdueOnly", overdueOnly);
        model.addAttribute("activePage", "shipments");
        return "manager/shipments";
    }

    @GetMapping("/stock")
    public String stock(
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(required = false, defaultValue = "false") boolean lowStockOnly,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {

        List<com.fullstockwh.product.dto.ProductResponse> products =
                productService.getAllProducts();

        if (!search.isBlank()) {
            String q = search.toLowerCase();
            products = products.stream()
                    .filter(p -> p.getName().toLowerCase().contains(q) ||
                            (p.getCategoryName() != null &&
                                    p.getCategoryName().toLowerCase().contains(q)))
                    .collect(Collectors.toList());
        }

        if (lowStockOnly) {
            products = products.stream()
                    .filter(p -> p.getTotalStock() <= StockThreshold.PRODUCT_LOW_STOCK)
                    .collect(Collectors.toList());
        }

        UserEntity manager = userService.findByEmail(userDetails.getUsername());

        model.addAttribute("products", products);
        model.addAttribute("search", search);
        model.addAttribute("lowStockOnly", lowStockOnly);
        model.addAttribute("lowStockThreshold", StockThreshold.PRODUCT_LOW_STOCK);
        model.addAttribute("myRequests", stockRequestService.getRequestsByManager(manager));
        model.addAttribute("activePage", "stock");
        return "manager/stock";
    }

    @GetMapping("/stock/{productId}")
    public String stockDetail(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {

        com.fullstockwh.product.dto.ProductResponse product =
                productService.getProductById(productId);

        List<com.fullstockwh.product.product_variant.dto.VariantResponse> variants =
                variantService.getVariantsByProductId(productId);

        UserEntity manager = userService.findByEmail(userDetails.getUsername());

        model.addAttribute("product", product);
        model.addAttribute("variants", variants);
        model.addAttribute("lowStockThreshold", StockThreshold.LOW_STOCK);
        model.addAttribute("myRequests", stockRequestService.getRequestsByManager(manager));
        model.addAttribute("activePage", "stock");
        return "manager/stock-detail";
    }

    @PostMapping("/stock/request")
    public String requestStock(@RequestParam Long variantId,
                               @RequestParam Integer quantity,
                               @RequestParam(required = false) String note,
                               @AuthenticationPrincipal UserDetails userDetails,
                               RedirectAttributes redirectAttributes) {
        try {
            UserEntity manager = userService.findByEmail(userDetails.getUsername());
            stockRequestService.createRequest(variantId, quantity, note, manager);
            redirectAttributes.addFlashAttribute("success", "Stock request submitted.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/manager/stock";
    }

    @PostMapping("/stock/bulk-request")
    public String bulkRequestStock(@RequestParam List<Long> variantIds,
                                   @RequestParam Integer quantity,
                                   @RequestParam(required = false) String note,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   RedirectAttributes redirectAttributes) {
        try {
            UserEntity manager = userService.findByEmail(userDetails.getUsername());
            stockRequestService.createBulkRequest(variantIds, quantity, note, manager);
            redirectAttributes.addFlashAttribute("success",
                    variantIds.size() + " variants requested successfully.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/manager/stock";
    }
}

package com.fullstockwh.shipment;

import com.fullstockwh.shipment.dto.ShipmentTrackResponse;
import com.fullstockwh.shipment.dto.ShipOrderRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.fullstockwh.shipment.dto.ShipmentStatsResponse;
import java.time.LocalDate;


@Controller
@RequiredArgsConstructor
public class ShipmentController
{
    private final ShipmentService shipmentService;
    private final ShipmentStatsService shipmentStatsService;
    private final com.fullstockwh.order.OrderRepository orderRepository;

    @GetMapping("/admin/orders/{orderId}/ship")
    @PreAuthorize("hasRole('ADMIN')")
    public String shipOrderPage(@PathVariable Long orderId, Model model)
    {
        model.addAttribute("orderId", orderId);
        model.addAttribute("request", new ShipOrderRequest());
        model.addAttribute("activePage", "orders");

        orderRepository.findById(orderId).ifPresent(order -> {
            com.fullstockwh.user.address.Address address = order.getShippingAddress();
            if (address != null) {
                model.addAttribute("destLat", address.getLatitude());
                model.addAttribute("destLon", address.getLongitude());
                model.addAttribute("destCity", address.getCity());
                model.addAttribute("destDistrict", address.getDistrict());
            }
        });

        return "admin/ship-order";
    }


    @PostMapping("/admin/orders/{orderId}/ship")
    @PreAuthorize("hasRole('ADMIN')")
    public String shipOrder(@PathVariable Long orderId,
                            @Valid @ModelAttribute ShipOrderRequest request,
                            RedirectAttributes redirectAttributes)
    {
        request.setOrderId(orderId);
        try {
            shipmentService.shipOrder(request);
            redirectAttributes.addFlashAttribute("success", "Order shipped successfully.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/orders";
    }


    @PostMapping("/admin/orders/{orderId}/deliver")
    @PreAuthorize("hasRole('ADMIN')")
    public String markDelivered(@PathVariable Long orderId,
                                RedirectAttributes redirectAttributes)
    {
        try {
            shipmentService.markDelivered(orderId);
            redirectAttributes.addFlashAttribute("success", "Order marked as delivered.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/orders";
    }
    @GetMapping("/admin/shipments")
    @PreAuthorize("hasRole('ADMIN')")
    public String shipmentsPage(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Model model)
    {
        if (from == null) from = LocalDate.of(2000, 1, 1);
        if (to == null) to = LocalDate.now();

        ShipmentStatsResponse stats = shipmentStatsService.getStats(from, to);
        model.addAttribute("stats",      stats);
        model.addAttribute("from",       from);
        model.addAttribute("to",         to);

        long diffDays = java.time.temporal.ChronoUnit.DAYS.between(from, to);
        String autoPeriod;
        if (diffDays <= 7)        autoPeriod = "daily";
        else if (diffDays <= 60)  autoPeriod = "weekly";
        else if (diffDays <= 365) autoPeriod = "monthly";
        else                      autoPeriod = "yearly";

        model.addAttribute("autoPeriod", autoPeriod);
        model.addAttribute("activePage", "shipments");

        return "admin/shipments";
    }

    @GetMapping("/orders/{orderId}/track")
    public String trackOrder(@PathVariable Long orderId, Model model)
    {
        try {
            ShipmentTrackResponse tracking = shipmentService.getTrackingInfo(orderId);
            model.addAttribute("tracking", tracking);
            model.addAttribute("orderId",  orderId);
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "track-order";
    }
}
package com.fullstockwh.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController
{
    @GetMapping("/")
    public String AdminDashboard()
    {
        return "admin/dashboard";
    }

    @GetMapping("/orders")
    public String showOrdersPage()
    {

    return "admin/orders";
    }
    @GetMapping("/analytics")
    public String showAnalyticsPage()
    {

    return "admin/analytics";
    }
    @GetMapping("/settings")
    public String showSettingsPage() {
        return "admin/settings";
    }
    @GetMapping("/employees")
    public String showEmployeesPage() {
        return "admin/employees";
    }
    @GetMapping("/catalog")
    public String showCatalogPage() {
        return "admin/catalog";
    }
    @GetMapping("/supplyrequests")
    public String showSupplyRequestsPage() {
        return "admin/supplyrequests";
    }
    @GetMapping("/pricing")
    public String showPricingPage() {
        return "admin/pricing";
    }
    @GetMapping("/sustainability")
    public String showSustainabilityPage() {
        return "admin/sustainability";
    }
}
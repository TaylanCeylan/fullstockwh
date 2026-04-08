package com.fullstockwh.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminDashboardController
{
    @GetMapping("/")
    public String AdminDashboard()
    {
        return "AdminDashboard";
    }
}

package com.fullstockwh.dashboard;

import com.fullstockwh.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController
{
    private final ProductService productService;

    @GetMapping("/")
    public String homePage(Model model)
    {
        model.addAttribute("products", productService.getAllProducts());
        return "index";
    }
}

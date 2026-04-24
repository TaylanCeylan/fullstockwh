package com.fullstockwh.dashboard;

import com.fullstockwh.product.Product;
import com.fullstockwh.product.ProductService;
import com.fullstockwh.product.dto.ProductCreateRequest;
import com.fullstockwh.product.dto.ProductResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController
{
    private final ProductService productService;

    public AdminDashboardController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/dashboard")
    public String AdminDashboard()
    {
        return "admin/dashboard";
    }

    @GetMapping("/products")
    public String adminProducts(Model model)
    {
        model.addAttribute("activePage", "products");
        model.addAttribute("product", new ProductCreateRequest());

        List<ProductResponse> products = productService.getAllProducts();
        model.addAttribute("productsList", products);

        return "admin/products";
    }

    @PostMapping("/products/add")
    public String addProduct(@ModelAttribute("product")  ProductCreateRequest productCreateRequest)
    {
        productService.createProduct(productCreateRequest);

        return "redirect:/admin/products";
    }
}
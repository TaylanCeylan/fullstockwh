package com.fullstockwh.dashboard;

import com.fullstockwh.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class HomeController
{
    private final ProductService productService;

    @GetMapping("/")
    public String homePage(
            @RequestParam(name = "search", required = false, defaultValue = "") String search,
            Model model)
    {

        var products = search.isBlank()
                ? productService.getAllProducts()
                : productService.searchAndSortProducts(search, "id", "asc");

        model.addAttribute("products", products);
        model.addAttribute("search", search);
        return "index";
    }
    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable Long id, Model model)
    {
        model.addAttribute("product", productService.getProductById(id));
        return "product-detail";
    }
    @GetMapping("/category/{name}")
    public String productsByCategory(@PathVariable String name, Model model)
    {
        model.addAttribute("products", productService.getProductsByCategoryName(name));
        model.addAttribute("pageTitle", name);
        model.addAttribute("activeCategory", name);
        model.addAttribute("pageTitle", "All Products");
        return "index";
    }
}

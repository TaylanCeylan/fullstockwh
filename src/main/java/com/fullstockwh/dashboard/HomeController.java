package com.fullstockwh.dashboard;

import com.fullstockwh.category.CategoryService;
import com.fullstockwh.category.dto.CategoryResponse;
import com.fullstockwh.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController
{
    private final ProductService productService;
    private final CategoryService categoryService;

    @GetMapping("/")
    public String homePage(
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(required = false, defaultValue = "") String gender,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false, defaultValue = "") String sort,
            Model model)
    {

        var products = productService.shopFilter(search, gender, categoryId, minPrice, maxPrice, sort);
        model.addAttribute("products", products);

        model.addAttribute("menCategories",    categoryService.getCategoriesByGender("MEN"));
        model.addAttribute("womenCategories",  categoryService.getCategoriesByGender("WOMEN"));
        model.addAttribute("unisexCategories", categoryService.getCategoriesByGender("UNISEX"));

        model.addAttribute("search",     search);
        model.addAttribute("gender",     gender);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("minPrice",   minPrice);
        model.addAttribute("maxPrice",   maxPrice);
        model.addAttribute("currentSort", sort);

        String pageTitle;
        if (search != null && !search.isBlank()) {
            pageTitle = "Results for \"" + search + "\"";
        } else if (categoryId != null) {

            String catName = products.stream()
                    .findFirst()
                    .map(p -> p.getCategoryName())
                    .orElse("");
            pageTitle = gender.isEmpty() ? catName : capitalize(gender) + " · " + catName;
        } else if (!gender.isBlank()) {
            pageTitle = capitalize(gender);
        } else {
            pageTitle = "All Products";
        }
        model.addAttribute("pageTitle", pageTitle);

        return "index";
    }

    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable Long id, Model model)
    {
        var product = productService.getProductById(id);
        model.addAttribute("product", product);

        if (product.getVariants() != null && !product.getVariants().isEmpty()) {

            var uniqueColors = product.getVariants().stream()
                    .map(v -> v.getColor())
                    .filter(c -> c != null)
                    .distinct()
                    .collect(java.util.stream.Collectors.toList());

            var uniqueSizes = product.getVariants().stream()
                    .map(v -> v.getSize())
                    .filter(s -> s != null)
                    .distinct()
                    .collect(java.util.stream.Collectors.toList());

            model.addAttribute("uniqueColors", uniqueColors);
            model.addAttribute("uniqueSizes", uniqueSizes);

        } else {
            model.addAttribute("uniqueColors", java.util.List.of());
            model.addAttribute("uniqueSizes", java.util.List.of());
        }

        return "product-detail";
    }
    @GetMapping("/category/{name}")
    public String productsByCategory(@PathVariable String name, Model model)
    {
        model.addAttribute("products", productService.getProductsByCategoryName(name));
        model.addAttribute("pageTitle", name);
        model.addAttribute("activeCategory", name);
        model.addAttribute("menCategories",    categoryService.getCategoriesByGender("MEN"));
        model.addAttribute("womenCategories",  categoryService.getCategoriesByGender("WOMEN"));
        model.addAttribute("unisexCategories", categoryService.getCategoriesByGender("UNISEX"));
        return "index";
    }

    private String capitalize(String s) {
        if (s == null || s.isBlank()) return s;
        return s.charAt(0) + s.substring(1).toLowerCase();
    }
}

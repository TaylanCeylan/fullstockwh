package com.fullstockwh.dashboard;

import com.fullstockwh.category.CategoryService;
import com.fullstockwh.product.ProductService;
import com.fullstockwh.product.dto.ProductResponse;
import com.fullstockwh.product.review.ReviewService;
import com.fullstockwh.product.review.dto.ReviewCreateRequest;
import com.fullstockwh.product.review.dto.ReviewResponse;
import com.fullstockwh.user.UserEntity;
import com.fullstockwh.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class HomeController
{
    private final ProductService  productService;
    private final CategoryService categoryService;
    private final ReviewService   reviewService;
    private final UserService     userService;

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
            String catName = products.stream().findFirst().map(ProductResponse::getCategoryName).orElse("");
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
    public String productDetail(@PathVariable Long id,
                                @AuthenticationPrincipal UserDetails userDetails,
                                Model model)
    {
        ProductResponse product = productService.getProductById(id);
        model.addAttribute("product", product);

        if (product.getVariants() != null && !product.getVariants().isEmpty()) {
            var uniqueColors = product.getVariants().stream()
                    .map(v -> v.getColor()).filter(c -> c != null).distinct()
                    .collect(Collectors.toList());
            var uniqueSizes = product.getVariants().stream()
                    .map(v -> v.getSize()).filter(s -> s != null).distinct()
                    .collect(Collectors.toList());
            model.addAttribute("uniqueColors", uniqueColors);
            model.addAttribute("uniqueSizes",  uniqueSizes);
        } else {
            model.addAttribute("uniqueColors", List.of());
            model.addAttribute("uniqueSizes",  List.of());
        }

        List<ReviewResponse> reviews = reviewService.getReviewsByProductId(id);
        model.addAttribute("reviews", reviews);
        model.addAttribute("reviewCount", reviews.size());

        double avg = reviews.stream().mapToInt(ReviewResponse::getRating).average().orElse(0.0);
        model.addAttribute("averageRating",
                BigDecimal.valueOf(avg).setScale(1, RoundingMode.HALF_UP));

        boolean canReview     = false;
        boolean alreadyReviewed = false;
        if (userDetails != null) {
            UserEntity user = userService.findByEmail(userDetails.getUsername());
            canReview       = reviewService.canUserReview(id, user);
            alreadyReviewed = !canReview && reviews.stream()
                    .anyMatch(r -> r.getUserName().equals(userDetails.getUsername()));
        }
        model.addAttribute("canReview",       canReview);
        model.addAttribute("alreadyReviewed", alreadyReviewed);
        model.addAttribute("isLoggedIn",      userDetails != null);

        return "product-detail";
    }

    @PostMapping("/product/{id}/reviews")
    public String submitReview(@PathVariable Long id,
                               @AuthenticationPrincipal UserDetails userDetails,
                               @ModelAttribute ReviewCreateRequest request,
                               RedirectAttributes redirectAttributes)
    {
        if (userDetails == null) return "redirect:/login";

        request.setProductId(id);
        try {
            UserEntity user = userService.findByEmail(userDetails.getUsername());
            reviewService.saveReview(request, user);
            redirectAttributes.addFlashAttribute("reviewSuccess", "Review Saved!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("reviewError", e.getMessage());
        }
        return "redirect:/product/" + id + "#reviews";
    }

    @GetMapping("/category/{name}")
    public String productsByCategory(@PathVariable String name, Model model)
    {
        model.addAttribute("products",         productService.getProductsByCategoryName(name));
        model.addAttribute("pageTitle",        name);
        model.addAttribute("activeCategory",   name);
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
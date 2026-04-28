package com.fullstockwh.dashboard;

import com.fullstockwh.category.CategoryService;
import com.fullstockwh.category.dto.CategoryCreateRequest;
import com.fullstockwh.category.dto.CategoryResponse;
import com.fullstockwh.category.dto.CategoryUpdateRequest;
import com.fullstockwh.category.enums.TargetGender;
import com.fullstockwh.product.Product;
import com.fullstockwh.category.Category;
import com.fullstockwh.category.CategoryRepository;
import com.fullstockwh.product.dto.ProductUpdateRequest;
import com.fullstockwh.product.product_variant.ProductVariant;
import com.fullstockwh.product.product_variant.VariantRepository;
import com.fullstockwh.product.product_variant.VariantService;
import com.fullstockwh.product.product_variant.dto.VariantCreateRequest;
import com.fullstockwh.product.product_variant.dto.VariantUpdateRequest;
import com.fullstockwh.product.product_variant.enums.Color;
import com.fullstockwh.product.product_variant.enums.Size;
import com.fullstockwh.product.ProductService;
import com.fullstockwh.product.dto.ProductCreateRequest;
import com.fullstockwh.product.dto.ProductResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController
{
    private final ProductService productService;
    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;
    private final VariantService variantService;
    private final VariantRepository variantRepository;
    public AdminDashboardController(ProductService productService,
                                    CategoryService categoryService,
                                    CategoryRepository categoryRepository,
                                    VariantService variantService,
                                    VariantRepository variantRepository) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.categoryRepository = categoryRepository;
        this.variantService = variantService;
        this.variantRepository = variantRepository;
    }

    @GetMapping("/dashboard")
    public String AdminDashboard()
    {
        return "admin/dashboard";
    }

    @GetMapping("/products")
    public String adminProducts(
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String direction,
            Model model)
    {
        model.addAttribute("activePage", "products");
        model.addAttribute("product", new ProductCreateRequest());
        model.addAttribute("categoriesList", categoryService.getAllCategories());
        model.addAttribute("genders", TargetGender.values());

        List<ProductResponse> products = productService.searchAndSortProducts(search, sortBy, direction);
        model.addAttribute("productsList", products);

        model.addAttribute("search", search);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        model.addAttribute("nextDirection", "asc".equals(direction) ? "desc" : "asc");
        return "admin/products";
    }
    @GetMapping("/categories")
    public String adminCategories(Model model)
    {
        model.addAttribute("activePage", "categories");
        model.addAttribute("categoryRequest", new CategoryCreateRequest());
        model.addAttribute("categoriesList", categoryService.getAllCategories());
        model.addAttribute("genders", TargetGender.values());
        return "admin/categories";
    }

    @PostMapping("/categories/add")
    public String addCategory(@ModelAttribute("categoryRequest") CategoryCreateRequest request)
    {
        categoryService.createCategory(request);
        return "redirect:/admin/categories";
    }

    @GetMapping("/categories/edit/{id}")
    public String showCategoryEditForm(@PathVariable Long id, Model model)
    {
        CategoryResponse existing = categoryService.getAllCategories().stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Category not found!"));

        CategoryUpdateRequest updateRequest = new CategoryUpdateRequest();
        updateRequest.setName(existing.getName());

        model.addAttribute("activePage", "categories");
        model.addAttribute("categoryRequest", new CategoryCreateRequest());
        model.addAttribute("updateRequest", updateRequest);
        model.addAttribute("editId", id);
        model.addAttribute("editGender", existing.getTargetGender());
        model.addAttribute("categoriesList", categoryService.getAllCategories());
        model.addAttribute("genders", TargetGender.values());
        return "admin/categories";
    }

    @PostMapping("/categories/update/{id}")
    public String updateCategory(@PathVariable Long id,
                                 @ModelAttribute("updateRequest") CategoryUpdateRequest request)
    {
        categoryService.updateCategoryName(id, request);
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable Long id)
    {
        categoryService.deleteCategory(id);
        return "redirect:/admin/categories";
    }

    @PostMapping("/products/add")
    public String addProduct(@ModelAttribute("product")  ProductCreateRequest productCreateRequest)
    {
        productService.createProduct(productCreateRequest);

        return "redirect:/admin/products";
    }
    @GetMapping("/products/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model)
    {
        ProductResponse existing = productService.getProductById(id);


        Category category = categoryRepository.findByName(existing.getCategoryName())
                .orElseThrow(() -> new RuntimeException("Category not found!"));

        ProductUpdateRequest updateRequest = new ProductUpdateRequest();
        updateRequest.setName(existing.getName());
        updateRequest.setDescription(existing.getDescription());
        updateRequest.setCategoryId(category.getId());
        updateRequest.setPrice(existing.getPrice());


        model.addAttribute("product", new ProductCreateRequest());
        model.addAttribute("activePage", "products");
        model.addAttribute("updateRequest", updateRequest);
        model.addAttribute("editId", id);
        model.addAttribute("categoriesList", categoryRepository.findAll());
        model.addAttribute("productsList", productService.getAllProducts());

        return "admin/products";
    }
    @PostMapping("/products/update/{id}")
    public String updateProduct(@PathVariable Long id,
                                @ModelAttribute("updateRequest") ProductUpdateRequest updateRequest)
    {
        productService.updateProduct(id, updateRequest);
        return "redirect:/admin/products";
    }
    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id)
    {
        productService.deleteProduct(id);
        return "redirect:/admin/products";
    }
    @GetMapping("/products/{id}/variants")
    public String manageVariants(@PathVariable Long id, Model model) {
        ProductResponse product = productService.getProductById(id);
        model.addAttribute("product", product);
        model.addAttribute("variants", variantService.getVariantsByProductId(id));
        model.addAttribute("variantRequest", new VariantCreateRequest());
        model.addAttribute("colors", Color.values());
        model.addAttribute("sizes", Size.values());
        return "admin/variants";
    }

    @PostMapping("/products/{id}/variants/add")
    public String addVariant(@PathVariable Long id,
                             @ModelAttribute("variantRequest") VariantCreateRequest request,
                             RedirectAttributes redirectAttributes) {
        request.setProductId(id);

        try {
            variantService.addVariantToProduct(request);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/admin/products/" + id + "/variants";
    }
    @PostMapping("/variants/delete/{variantId}")
    public String deleteVariant(@PathVariable Long variantId,
                                @RequestParam Long productId) {
        variantService.deleteVariant(variantId);
        return "redirect:/admin/products/" + productId + "/variants";
    }
    @GetMapping("/variants/edit/{id}")
    public String showEditVariantForm(@PathVariable Long id, Model model) {
        ProductVariant variant = variantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Variant not found!"));

        VariantUpdateRequest updateRequest = new VariantUpdateRequest();
        updateRequest.setStockQuantity(variant.getStockQuantity());
        updateRequest.setUnitWeight(variant.getUnitWeight());

        model.addAttribute("variant", variant);
        model.addAttribute("updateRequest", updateRequest);
        model.addAttribute("product", productService.getProductById(variant.getProduct().getId()));
        model.addAttribute("variants", variantService.getVariantsByProductId(variant.getProduct().getId()));
        model.addAttribute("variantRequest", new VariantCreateRequest());
        model.addAttribute("colors", Color.values());
        model.addAttribute("sizes", Size.values());
        return "admin/variants";
    }

    @PostMapping("/variants/update/{id}")
    public String updateVariant(@PathVariable Long id,
                                @ModelAttribute("updateRequest") VariantUpdateRequest request,
                                @RequestParam Long productId) {
        variantService.updateVariant(request, id);
        return "redirect:/admin/products/" + productId + "/variants";
    }
}
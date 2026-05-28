package com.fullstockwh.dashboard;

import com.fullstockwh.category.CategoryService;
import com.fullstockwh.category.dto.CategoryCreateRequest;
import com.fullstockwh.category.dto.CategoryResponse;
import com.fullstockwh.category.dto.CategoryUpdateRequest;
import com.fullstockwh.category.enums.TargetGender;
import com.fullstockwh.order.Order;
import com.fullstockwh.order.OrderRepository;
import com.fullstockwh.order.OrderService;
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
import com.fullstockwh.common.FileStorageService;
import com.fullstockwh.product.ProductImage;
import com.fullstockwh.product.ProductImageRepository;
import com.fullstockwh.user.UserService;
import com.fullstockwh.user.dto.AdminUserCreateRequest;
import com.fullstockwh.user.dto.AdminUserUpdateRequest;
import com.fullstockwh.user.dto.UserResponse;
import com.fullstockwh.auth.enums.Role;
import org.springframework.web.multipart.MultipartFile;
import com.fullstockwh.stock.StockRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDashboardController
{
    private final ProductService productService;
    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;
    private final VariantService variantService;
    private final VariantRepository variantRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final FileStorageService fileStorageService;
    private final ProductImageRepository productImageRepository;
    private final StockRequestService stockRequestService;
    private final UserService userService;

    @GetMapping("/dashboard")
    public String AdminDashboard(Model model) {
        List<CategoryResponse> categories = categoryService.getAllCategories();
        List<ProductResponse> products = productService.getAllProducts();

        int totalStock = products.stream()
                .mapToInt(ProductResponse::getTotalStock)
                .sum();

        Map<String, Long> categoryStats = products.stream()
                .collect(Collectors.groupingBy(
                        ProductResponse::getCategoryName,
                        Collectors.counting()
                ));

        categories.forEach(c -> categoryStats.putIfAbsent(c.getName(), 0L));

        model.addAttribute("totalProducts", products.size());
        model.addAttribute("totalStock", totalStock);
        model.addAttribute("totalCategories", categories.size());
        model.addAttribute("categoryStats", categoryStats);

        List<Order> recentOrders = orderRepository.findRecentOrdersWithDetails(PageRequest.of(0, 10));
        model.addAttribute("recentOrders", recentOrders);
        List<ProductVariant> lowStockVariants = variantRepository.findByStockQuantityLessThan(5);
        model.addAttribute("lowStockVariants", lowStockVariants);
        model.addAttribute("lowStockCount", lowStockVariants.size());
        model.addAttribute("activePage", "dashboard");
        return "admin/dashboard";
    }

    @GetMapping("/products")
    public String adminProducts(
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(required = false, defaultValue = "") String gender,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String direction,
            @RequestParam(required = false, defaultValue = "false") boolean lowStockOnly,
            Model model)
    {
        model.addAttribute("activePage", "products");
        model.addAttribute("product", new ProductCreateRequest());
        model.addAttribute("categoriesList", categoryService.getAllCategories());
        model.addAttribute("genders", TargetGender.values());

        List<ProductResponse> products = productService.filterProducts(search, gender, categoryId, sortBy, direction, lowStockOnly);
        model.addAttribute("productsList", products);

        model.addAttribute("search", search);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        model.addAttribute("lowStockOnly", lowStockOnly);
        model.addAttribute("nextDirection", "asc".equals(direction) ? "desc" : "asc");
        return "admin/products";
    }
    @GetMapping("/categories")
    public String adminCategories(
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(required = false, defaultValue = "") String gender,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String direction,
            Model model)
    {
        model.addAttribute("activePage", "categories");
        model.addAttribute("categoryRequest", new CategoryCreateRequest());
        model.addAttribute("categoriesList",
                categoryService.filterCategories(search, gender, sortBy, direction));
        model.addAttribute("genders", TargetGender.values());
        model.addAttribute("search", search);
        model.addAttribute("selectedGender", gender);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        model.addAttribute("nextDirection", "asc".equals(direction) ? "desc" : "asc");
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

    @GetMapping("/orders")
    public String adminOrders(
            @RequestParam(required = false, defaultValue = "") String status,
            Model model)
    {
        List<Order> allOrders;
        if (status != null && !status.isBlank()) {
            com.fullstockwh.order.enums.OrderStatus orderStatus =
                    com.fullstockwh.order.enums.OrderStatus.valueOf(status);
            allOrders = orderRepository.findAll().stream()
                    .filter(o -> o.getStatus() == orderStatus)
                    .sorted((a, b) -> b.getOrderDate().compareTo(a.getOrderDate()))
                    .collect(java.util.stream.Collectors.toList());
        } else {
            allOrders = orderRepository.findRecentOrdersWithDetails(
                    PageRequest.of(0, 200));
        }
        model.addAttribute("allOrders", allOrders);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("activePage", "orders");
        return "admin/orders";
    }

    @GetMapping("/products/{id}/variants")
    public String manageVariants(@PathVariable Long id,
                                 @RequestParam(required = false, defaultValue = "false") boolean lowStockOnly,
                                 Model model) {
        ProductResponse product = productService.getProductById(id);
        model.addAttribute("product", product);

        var variants = variantService.getVariantsByProductId(id);
        if (lowStockOnly) {
            variants = variants.stream()
                    .filter(v -> v.getStockQuantity() <= 10)
                    .collect(java.util.stream.Collectors.toList());
        }

        model.addAttribute("variants", variants);
        model.addAttribute("lowStockOnly", lowStockOnly);
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


    @GetMapping("/products/{id}/images")
    public String manageProductImages(@PathVariable Long id, Model model)
    {
        ProductResponse product = productService.getProductById(id);
        List<ProductImage> images = productImageRepository
                .findByProductIdOrderByDisplayOrderAsc(id);

        model.addAttribute("product", product);
        model.addAttribute("images", images);
        model.addAttribute("activePage", "products");
        return "admin/product-images";
    }


    @PostMapping("/products/{id}/images/add")
    public String addProductImage(@PathVariable Long id,
                                  @RequestParam("imageFile") MultipartFile imageFile,
                                  RedirectAttributes redirectAttributes)
    {
        if (imageFile == null || imageFile.isEmpty()) {
            redirectAttributes.addFlashAttribute("imageError", "Please select a file.");
            return "redirect:/admin/products/" + id + "/images";
        }
        try {
            String imageUrl = fileStorageService.store(imageFile);
            long count = productImageRepository
                    .findByProductIdOrderByDisplayOrderAsc(id).size();

            Product product = new Product();
            product.setId(id);

            ProductImage image = ProductImage.builder()
                    .product(product)
                    .imageUrl(imageUrl)
                    .displayOrder((int) count)
                    .build();

            productImageRepository.save(image);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("imageError", e.getMessage());
        }
        return "redirect:/admin/products/" + id + "/images";
    }

    @PostMapping("/products/{id}/images/delete/{imageId}")
    public String deleteProductImage(@PathVariable Long id,
                                     @PathVariable Long imageId)
    {
        productImageRepository.findById(imageId).ifPresent(image -> {
            fileStorageService.delete(image.getImageUrl());
            productImageRepository.delete(image);
        });
        return "redirect:/admin/products/" + id + "/images";
    }

    @PostMapping("/products/{id}/images/set-main/{imageId}")
    public String setMainImage(@PathVariable Long id,
                               @PathVariable Long imageId)
    {
        List<ProductImage> images = productImageRepository
                .findByProductIdOrderByDisplayOrderAsc(id);

        int order = 0;

        ProductImage selected = null;
        for (ProductImage img : images) {
            if (img.getId().equals(imageId)) {
                selected = img;
                break;
            }
        }
        if (selected != null) {
            selected.setDisplayOrder(0);
            productImageRepository.save(selected);
            order = 1;
            for (ProductImage img : images) {
                if (!img.getId().equals(imageId)) {
                    img.setDisplayOrder(order++);
                    productImageRepository.save(img);
                }
            }
        }
        return "redirect:/admin/products/" + id + "/images";
    }

    @PostMapping("/orders/{id}/cancel")
    public String adminCancelOrder(@PathVariable Long id,
                                   RedirectAttributes redirectAttributes) {
        try {
            orderService.adminCancelOrder(id);
            redirectAttributes.addFlashAttribute("success",
                    "Order #" + id + " cancelled.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/orders";
    }

    @GetMapping("/stock-requests")
    public String stockRequests(Model model) {
        model.addAttribute("requests", stockRequestService.getAllRequests());
        model.addAttribute("activePage", "stockRequests");
        return "admin/stock-requests";
    }

    @PostMapping("/stock-requests/{id}/approve")
    public String approveRequest(@PathVariable Long id,
                                 RedirectAttributes redirectAttributes) {
        try {
            stockRequestService.approveRequest(id);
            redirectAttributes.addFlashAttribute("success", "Request approved.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/stock-requests";
    }

    @PostMapping("/stock-requests/{id}/reject")
    public String rejectRequest(@PathVariable Long id,
                                RedirectAttributes redirectAttributes) {
        try {
            stockRequestService.rejectRequest(id);
            redirectAttributes.addFlashAttribute("success", "Request rejected.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/stock-requests";
    }

    @GetMapping("/users")
    public String adminUsers(
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(required = false, defaultValue = "") String role,
            @RequestParam(required = false, defaultValue = "") String status,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String direction,
            Model model) {

        List<UserResponse> users = userService.getAllUsers().stream()
                .filter(u -> search.isBlank() ||
                        (u.getFirstName() != null && u.getFirstName().toLowerCase().contains(search.toLowerCase())) ||
                        (u.getLastName() != null && u.getLastName().toLowerCase().contains(search.toLowerCase())) ||
                        u.getEmail().toLowerCase().contains(search.toLowerCase()))
                .filter(u -> role.isBlank() || u.getRole().name().equals(role))
                .filter(u -> status.isBlank() ||
                        (status.equals("active") && u.isEnabled()) ||
                        (status.equals("inactive") && !u.isEnabled()))
                .sorted((a, b) -> {
                    int cmp = switch (sortBy) {
                        case "name" -> {
                            String nameA = (a.getFirstName() != null ? a.getFirstName() : "") +
                                    (a.getLastName() != null ? a.getLastName() : "");
                            String nameB = (b.getFirstName() != null ? b.getFirstName() : "") +
                                    (b.getLastName() != null ? b.getLastName() : "");
                            yield nameA.compareToIgnoreCase(nameB);
                        }
                        default -> a.getId().compareTo(b.getId());
                    };
                    return direction.equals("desc") ? -cmp : cmp;
                })
                .collect(java.util.stream.Collectors.toList());

        model.addAttribute("users", users);
        model.addAttribute("roles", Role.values());
        model.addAttribute("createRequest", new AdminUserCreateRequest());
        model.addAttribute("search", search);
        model.addAttribute("selectedRole", role);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        model.addAttribute("nextDirection", "asc".equals(direction) ? "desc" : "asc");
        model.addAttribute("activePage", "users");
        return "admin/users";
    }

    @PostMapping("/users/add")
    public String addUser(@ModelAttribute("createRequest") AdminUserCreateRequest request,
                          RedirectAttributes redirectAttributes) {
        try {
            userService.adminCreateUser(request);
            redirectAttributes.addFlashAttribute("success", "User created successfully.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/users/edit/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        UserResponse user = userService.getAllUsers().stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User not found"));

        AdminUserUpdateRequest updateRequest = new AdminUserUpdateRequest();
        updateRequest.setFirstName(user.getFirstName());
        updateRequest.setLastName(user.getLastName());

        model.addAttribute("editUser", user);
        model.addAttribute("updateRequest", updateRequest);
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("roles", Role.values());
        model.addAttribute("createRequest", new AdminUserCreateRequest());
        model.addAttribute("activePage", "users");
        return "admin/users";
    }

    @PostMapping("/users/update/{id}")
    public String updateUser(@PathVariable Long id,
                             @ModelAttribute("updateRequest") AdminUserUpdateRequest request,
                             RedirectAttributes redirectAttributes) {
        try {
            userService.adminUpdateUser(id, request);
            redirectAttributes.addFlashAttribute("success", "User updated successfully.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/toggle/{id}")
    public String toggleUser(@PathVariable Long id,
                             RedirectAttributes redirectAttributes) {
        try {
            userService.toggleUserEnabled(id);
            redirectAttributes.addFlashAttribute("success", "User status updated.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id,
                             RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("success", "User deleted.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }
}
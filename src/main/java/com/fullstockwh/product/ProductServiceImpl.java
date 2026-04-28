package com.fullstockwh.product;


import com.fullstockwh.product.dto.ProductCreateRequest;
import com.fullstockwh.product.dto.ProductUpdateRequest;
import com.fullstockwh.product.dto.ProductResponse;
import com.fullstockwh.category.Category;
import com.fullstockwh.category.CategoryRepository;
import com.fullstockwh.product.product_variant.VariantService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fullstockwh.product.product_variant.ProductVariant;
import com.fullstockwh.product.product_variant.VariantRepository;


import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class ProductServiceImpl implements ProductService
{
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final VariantService variantService;
    private final VariantRepository variantRepository;

    @Override
    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found!"));

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .category(category)
                .build();
        Product savedProduct = productRepository.save(product);



        return mapToResponse(savedProduct);
    }


    private String generateSkuForProduct(String name) {
        String prefix = (name != null && name.length() >= 3)
                ? name.substring(0, 3).toUpperCase()
                : "PRD";
        int randomNum = (int) (Math.random() * 9000) + 1000;
        return String.format("%s-%d", prefix, randomNum);
    }


    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductUpdateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found!"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found!"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setCategory(category);

        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }


        return mapToResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found to be deleted!"));

        // All variants will be deleted under this product
        productRepository.delete(product);
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    @Override
    public List<ProductResponse> searchAndSortProducts(String search, String sortBy, String direction) {


        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;


        String sortColumn = switch (sortBy != null ? sortBy : "") {
            case "name" -> "name";
            case "id"   -> "id";
            default     -> "id";
        };

        Sort sort = Sort.by(sortDirection, sortColumn);


        String keyword = (search == null || search.isBlank()) ? "" : search;

        return productRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword, sort)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found!"));
        return mapToResponse(product);
    }
    @Override
    public List<ProductResponse> getProductsByCategoryName(String categoryName) {
        return productRepository.findByCategory_Name(categoryName)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // entity to dto mapping
    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()

                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .categoryName(product.getCategory().getName())
                .gender(product.getCategory().getTargetGender().name())

                .variants(product.getVariants() == null ? Collections.emptyList() :
                        product.getVariants().stream()
                                .map(variantService::mapToVariantResponse)
                                .collect(Collectors.toList()))

                .build();
    }
}

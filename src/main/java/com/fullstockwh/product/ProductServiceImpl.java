package com.fullstockwh.product;

import com.fullstockwh.product.dto.ProductCreateRequest;
import com.fullstockwh.product.dto.ProductUpdateRequest;
import com.fullstockwh.product.dto.ProductResponse;
import com.fullstockwh.category.Category;
import com.fullstockwh.category.CategoryRepository;
import com.fullstockwh.product.product_variant.VariantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found! ID: " + request.getCategoryId()));

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(category)
                .build();

        return mapToResponse(productRepository.save(product));
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
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found!"));
        return mapToResponse(product);
    }

    // entity to dto mapping
    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()

                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .categoryName(product.getCategory().getName())
                .gender(product.getCategory().getTargetGender().name())

                .variants(product.getVariants() == null ? Collections.emptyList() :
                        product.getVariants().stream()
                                .map(variantService::mapToVariantResponse)
                                .collect(Collectors.toList()))

                .build();
    }
}

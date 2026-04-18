package com.fullstockwh.category;

import com.fullstockwh.category.dto.CategoryCreateRequest;
import com.fullstockwh.category.dto.CategoryUpdateRequest;
import com.fullstockwh.category.dto.CategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class CategoryServiceImpl implements CategoryService
{
    private final CategoryRepository categoryRepository;

    @Override
    public CategoryResponse createCategory(CategoryCreateRequest request) {
        // Check for category name - for no duplication
        categoryRepository.findByName(request.getName()).ifPresent(c -> {
            throw new RuntimeException("This category already exists!");
        });

        Category category = Category.builder()
                .name(request.getName())
                .targetGender(request.getTargetGender())
                .build();

        Category saved = categoryRepository.save(category);
        return mapToResponse(saved);
    }

    @Override
    public CategoryResponse updateCategoryName(Long id, CategoryUpdateRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found!"));

        if (categoryRepository.existsByNameIgnoreCaseAndTargetGender(request.getName(), category.getTargetGender())) {
            throw new RuntimeException("This category already exists in this Gender!");
        }

        category.setName(request.getName());

        Category saved = categoryRepository.save(category);
        return mapToResponse(saved);
    }

    @Override
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Category does not exist to be deleted!");
        }
        categoryRepository.deleteById(id);
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // convert entity to DTO
    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .targetGender(category.getTargetGender() != null ? category.getTargetGender().name() : null)
                .build();
    }
}

package com.fullstockwh.service.impl;

import com.fullstockwh.dto.request.CategoryCreateRequest;
import com.fullstockwh.dto.request.CategoryUpdateRequest;
import com.fullstockwh.dto.response.CategoryResponse;
import com.fullstockwh.entity.Category;
import com.fullstockwh.repository.CategoryRepository;
import com.fullstockwh.service.ICategoryService;
import com.fullstockwh.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService
{
    private final CategoryRepository categoryRepository;

    @Override
    public CategoryResponse createCategory(CategoryCreateRequest request) {
        // Check for category name - for no duplication
        categoryRepository.findByNameAndTargetGender(request.getName(), request.getTargetGender())
                .ifPresent(c -> {
                    throw new RuntimeException("Category '" + request.getName() + "' already exists for " + request.getTargetGender());
                });

        Category category = Category.builder()
                .name(request.getName())
                .code(request.getCode().toUpperCase())
                .targetGender(request.getTargetGender())
                .requiredAttributes(request.getRequiredAttributes() != null
                        ? request.getRequiredAttributes()
                        : new ArrayList<>()) // If it's null, attach empty list
                .build();

        Category savedCategory = categoryRepository.save(category);
        return mapToResponse(savedCategory);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryUpdateRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found!"));

        category.setName(request.getName());
        category.setTargetGender(request.getTargetGender());

        return mapToResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found!"));

        //SAFETY CHECK: cannot delete a category that having products!
        if (!category.getProducts().isEmpty()) {
            throw new RuntimeException("Cannot delete category! It contains " +
                    category.getProducts().size() + " active products.");
        }

        categoryRepository.deleteById(id);
    }


    // convert entity to DTO
    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .code(category.getCode())
                .targetGender(category.getTargetGender())
                .requiredAttributes(category.getRequiredAttributes())
                .build();
    }


    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryResponse> searchCategories(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAllCategories();
        }

        return categoryRepository.searchCategories(keyword).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
}

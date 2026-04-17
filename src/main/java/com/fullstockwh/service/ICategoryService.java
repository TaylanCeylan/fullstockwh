package com.fullstockwh.service;

import com.fullstockwh.dto.request.CategoryCreateRequest;
import com.fullstockwh.dto.request.CategoryUpdateRequest;
import com.fullstockwh.dto.response.CategoryResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

public interface ICategoryService
{
    @PreAuthorize("hasRole('ADMIN')") // Only Admin can CREATE Category
    CategoryResponse createCategory(CategoryCreateRequest request);

    @PreAuthorize("hasRole('ADMIN')") // Only Admin can DELETE Category
    void deleteCategory(Long id);

    @PreAuthorize("hasRole('ADMIN')")// Only Admin can UPDATE Category
    CategoryResponse updateCategory(Long id, CategoryUpdateRequest request);

    List<CategoryResponse> getAllCategories(); // Everyone can list Categories

    List<CategoryResponse> searchCategories(String keyword);
}

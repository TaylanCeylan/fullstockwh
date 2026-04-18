package com.fullstockwh.service;

import com.fullstockwh.dto.request.CategoryCreateRequest;
import com.fullstockwh.dto.request.CategoryUpdateRequest;
import com.fullstockwh.dto.response.CategoryResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

public interface ICategoryService
{
    @PreAuthorize("hasRole('ADMIN')")
    CategoryResponse createCategory(CategoryCreateRequest request);

    @PreAuthorize("hasRole('ADMIN')")
    CategoryResponse updateCategoryName(Long id, CategoryUpdateRequest request);

    @PreAuthorize("hasRole('ADMIN')")
    void deleteCategory(Long id);

    List<CategoryResponse> getAllCategories();
}

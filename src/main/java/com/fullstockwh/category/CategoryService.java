package com.fullstockwh.category;

import com.fullstockwh.category.dto.CategoryCreateRequest;
import com.fullstockwh.category.dto.CategoryUpdateRequest;
import com.fullstockwh.category.dto.CategoryResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

public interface CategoryService
{
    @PreAuthorize("hasRole('ADMIN')")
    CategoryResponse createCategory(CategoryCreateRequest request);

    @PreAuthorize("hasRole('ADMIN')")
    CategoryResponse updateCategoryName(Long id, CategoryUpdateRequest request);

    @PreAuthorize("hasRole('ADMIN')")
    void deleteCategory(Long id);

    List<CategoryResponse> getAllCategories();

    List<CategoryResponse> getCategoriesByGender(String gender);

    List<CategoryResponse> filterCategories(String keyword, String gender,
                                            String sortBy, String direction);
}

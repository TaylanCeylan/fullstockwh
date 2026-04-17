package com.fullstockwh.repository;

import com.fullstockwh.entity.Product;
import com.fullstockwh.enums.TargetGender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>
{
    Optional<Product> findByName(String name);

    //Retrieve products by gender through the category (Example: Men's products only)
    List<Product> findByCategory_TargetGender(TargetGender targetGender);

    //Retrieve products based on a specific category ID (Example: Only 'Jackets')
    List<Product> findByCategoryId(Long categoryId);

    //Search by name (use the query "like" for the search bar)
    List<Product> findByNameContainingIgnoreCase(String name);

    @Query("SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN p.category c " +
            "LEFT JOIN p.variants v " +
            "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(v.sku) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchProducts(@Param("keyword") String keyword);
}

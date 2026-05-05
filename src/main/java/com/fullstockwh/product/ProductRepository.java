package com.fullstockwh.product;

import com.fullstockwh.category.enums.TargetGender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>
{
    Optional<Product> findByName(String name);

    List<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(

            String name, String description, Sort sort);

    List<Product> findByCategory_Name(String categoryName);


    @Query("SELECT p FROM Product p JOIN p.category c " +
            "WHERE (:gender IS NULL OR c.targetGender = :gender) " +
            "AND (:keyword = '' OR " +
            "     LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "     LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "     LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Product> searchAllCategories(@Param("keyword") String keyword,
                                      @Param("gender") TargetGender gender,
                                      Sort sort);

    @Query("SELECT p FROM Product p JOIN p.category c " +
            "WHERE c.id = :categoryId " +
            "AND (:keyword = '' OR " +
            "     LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "     LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Product> searchWithinCategory(@Param("keyword") String keyword,
                                       @Param("categoryId") Long categoryId,
                                       Sort sort);

    @Query("SELECT p FROM Product p JOIN p.category c " +
            "WHERE (:keyword = '' OR " +
            "       LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "       LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "       LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "       LOWER(c.targetGender) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:gender IS NULL OR c.targetGender = :gender) " +
            "AND (:categoryId IS NULL OR c.id = :categoryId) " +
            "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR p.price <= :maxPrice)")
    List<Product> shopFilter(@Param("keyword") String keyword,
                             @Param("gender") TargetGender gender,
                             @Param("categoryId") Long categoryId,
                             @Param("minPrice") BigDecimal minPrice,
                             @Param("maxPrice") BigDecimal maxPrice,
                             Sort sort);
}

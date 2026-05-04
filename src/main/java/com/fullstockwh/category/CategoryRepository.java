package com.fullstockwh.category;

import com.fullstockwh.category.enums.TargetGender;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;


import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>
{
    Optional<Category> findByName(String name);
    boolean existsByNameIgnoreCaseAndTargetGender(String name, TargetGender targetGender);

    List<Category> findByTargetGender(TargetGender targetGender);

    @Query("SELECT c FROM Category c " +
            "WHERE (:keyword = '' OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:gender IS NULL OR c.targetGender = :gender)")
    List<Category> searchCategories(@Param("keyword") String keyword,
                                    @Param("gender") TargetGender gender,
                                    Sort sort);
}

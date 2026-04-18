package com.fullstockwh.category;

import com.fullstockwh.category.enums.TargetGender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>
{
    Optional<Category> findByName(String name);
    boolean existsByNameIgnoreCaseAndTargetGender(String name, TargetGender targetGender);
}

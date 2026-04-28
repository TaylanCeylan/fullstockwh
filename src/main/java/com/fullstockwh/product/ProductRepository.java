package com.fullstockwh.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>
{
    Optional<Product> findByName(String name);
    List<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String name, String description, Sort sort);
    List<Product> findByCategory_Name(String categoryName);
}

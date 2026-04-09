package com.fullstockwh.repository;

import com.fullstockwh.entity.Product;
import com.fullstockwh.enums.Color;
import com.fullstockwh.enums.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IProductRepository extends JpaRepository<Product, Long>
{
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByColorAndSize(Color color, Size size);
    List<Product> findByCategoryId(Long categoryId);
}

package com.fullstockwh.product.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>
{
    @Query("SELECT r FROM Review r JOIN FETCH r.userEntity WHERE r.product.id = :productId ORDER BY r.createdAt DESC")
    List<Review> findByProductIdWithUser(@Param("productId") Long productId);

    boolean existsByProductIdAndUserEntityId(Long productId, Long userId);

    @Query("SELECT r FROM Review r JOIN FETCH r.userEntity JOIN FETCH r.product ORDER BY r.createdAt DESC")
    List<Review> findAllWithDetails();
}
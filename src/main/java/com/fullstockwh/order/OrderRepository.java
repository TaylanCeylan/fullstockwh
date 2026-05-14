package com.fullstockwh.order;

import com.fullstockwh.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>
{
    @Query("SELECT o FROM Order o " +
            "LEFT JOIN FETCH o.shippingAddress " +
            "LEFT JOIN FETCH o.items i " +
            "LEFT JOIN FETCH i.productVariant v " +
            "LEFT JOIN FETCH v.product " +
            "WHERE o.user = :user " +
            "ORDER BY o.orderDate DESC")
    List<Order> findByUserWithItems(@Param("user") UserEntity user);
    List<Order> findByShippingAddressId(Long addressId);

    @Query("SELECT DISTINCT o FROM Order o " +
            "LEFT JOIN FETCH o.user " +
            "LEFT JOIN FETCH o.items i " +
            "LEFT JOIN FETCH i.productVariant v " +
            "LEFT JOIN FETCH v.product " +
            "ORDER BY o.orderDate DESC")
    List<Order> findRecentOrdersWithDetails(org.springframework.data.domain.Pageable pageable);
}


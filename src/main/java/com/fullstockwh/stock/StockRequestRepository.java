package com.fullstockwh.stock;

import com.fullstockwh.stock.enums.StockRequestStatus;
import com.fullstockwh.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRequestRepository extends JpaRepository<StockRequest, Long>
{
    List<StockRequest> findByRequestedByOrderByCreatedAtDesc(UserEntity user);
    List<StockRequest> findByStatusOrderByCreatedAtDesc(StockRequestStatus status);
    List<StockRequest> findAllByOrderByCreatedAtDesc();
}

package com.fullstockwh.stock;

import com.fullstockwh.user.UserEntity;

import java.util.List;

public interface StockRequestService
{
    void createRequest(Long variantId, Integer quantity, String note, UserEntity manager);
    void approveRequest(Long requestId);
    void rejectRequest(Long requestId);
    List<StockRequest> getRequestsByManager(UserEntity manager);
    List<StockRequest> getAllRequests();
    long getPendingCount();
    void createBulkRequest(List<Long> variantIds, Integer quantity, String note, UserEntity manager);
}

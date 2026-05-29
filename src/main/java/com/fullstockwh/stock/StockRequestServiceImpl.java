package com.fullstockwh.stock;


import com.fullstockwh.product.product_variant.ProductVariant;
import com.fullstockwh.product.product_variant.VariantRepository;
import com.fullstockwh.stock.enums.StockRequestStatus;
import com.fullstockwh.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockRequestServiceImpl implements StockRequestService
{
    private final StockRequestRepository stockRequestRepository;
    private final VariantRepository variantRepository;

    @Override
    @Transactional
    public void createRequest(Long variantId, Integer quantity, String note, UserEntity manager) {
        ProductVariant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Variant not found"));

        StockRequest request = StockRequest.builder()
                .variant(variant)
                .requestedBy(manager)
                .requestedQuantity(quantity)
                .note(note)
                .status(StockRequestStatus.PENDING)
                .build();

        stockRequestRepository.save(request);

    }

    @Override
    @Transactional
    public void approveRequest(Long requestId) {
        StockRequest request = stockRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (request.getStatus() != StockRequestStatus.PENDING)
            throw new RuntimeException("Only pending requests can be approved");

        ProductVariant variant = request.getVariant();
        variant.setStockQuantity(variant.getStockQuantity() + request.getRequestedQuantity());
        variantRepository.save(variant);

        request.setStatus(StockRequestStatus.APPROVED);
        request.setResolvedAt(LocalDateTime.now());
        stockRequestRepository.save(request);

    }

    @Override
    @Transactional
    public void rejectRequest(Long requestId) {
        StockRequest request = stockRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (request.getStatus() != StockRequestStatus.PENDING)
            throw new RuntimeException("Only pending requests can be rejected");

        request.setStatus(StockRequestStatus.REJECTED);
        request.setResolvedAt(LocalDateTime.now());
        stockRequestRepository.save(request);

    }

    @Override
    public List<StockRequest> getRequestsByManager(UserEntity manager) {
        return stockRequestRepository.findByRequestedByOrderByCreatedAtDesc(manager);
    }

    @Override
    public List<StockRequest> getAllRequests() {
        return stockRequestRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    public long getPendingCount() {
        return stockRequestRepository
                .findByStatusOrderByCreatedAtDesc(StockRequestStatus.PENDING)
                .size();
    }

    @Override
    public void createBulkRequest(List<Long> variantIds, Integer quantity, String note, UserEntity manager) {
        variantIds.forEach(variantId -> createRequest(variantId, quantity, note, manager));
    }
}

package com.fullstockwh.order;

import com.fullstockwh.order.dto.PaymentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService
{
    @Override
    public boolean processPayment(PaymentRequest request) {

        if (request.getCardNumber() == null || request.getCardNumber().isBlank())
            return false;
        if (request.getCvv() == null || request.getCvv().isBlank())
            return false;
        if (request.getExpiryDate() == null || request.getExpiryDate().isBlank())
            return false;
        return true;
    }
}

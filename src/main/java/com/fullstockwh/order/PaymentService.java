package com.fullstockwh.order;

import com.fullstockwh.order.dto.PaymentRequest;

public interface PaymentService
{
    boolean processPayment(PaymentRequest request);
}

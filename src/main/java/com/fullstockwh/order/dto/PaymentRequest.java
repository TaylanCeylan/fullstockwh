package com.fullstockwh.order.dto;

import lombok.Data;

@Data
public class PaymentRequest
{
    private String cardholderName;
    private String cardNumber;
    private String expiryDate;
    private String cvv;
}

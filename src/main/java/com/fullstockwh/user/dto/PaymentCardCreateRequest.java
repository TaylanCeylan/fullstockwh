package com.fullstockwh.user.dto;

import lombok.Data;

@Data
public class PaymentCardCreateRequest
{
    private String  cardNickname;
    private String  cardholderName;
    private String  lastFourDigits;
    private String  expiryDate;
    private boolean isTemporary = false;
}

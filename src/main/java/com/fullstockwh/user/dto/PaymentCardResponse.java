package com.fullstockwh.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentCardResponse
{
    private Long   id;
    private String cardNickname;
    private String cardholderName;
    private String lastFourDigits;
    private String expiryDate;
    private String maskedNumber;
}

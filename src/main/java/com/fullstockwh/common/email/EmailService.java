package com.fullstockwh.common.email;

import java.time.LocalDateTime;

public interface EmailService
{
    void SendVerificationEmail(String email, String token);
    void sendShippingConfirmationEmail(String email,
                                       String trackingNumber,
                                       String carrierName,
                                       LocalDateTime estimatedDelivery);
}

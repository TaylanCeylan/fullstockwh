package com.fullstockwh.common.email;

public interface EmailService
{
    void SendVerificationEmail(String email, String token);
}

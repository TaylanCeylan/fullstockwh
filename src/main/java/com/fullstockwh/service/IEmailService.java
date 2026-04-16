package com.fullstockwh.service;

public interface IEmailService
{
    void SendVerificationEmail(String email, String token);
}

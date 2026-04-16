package com.fullstockwh.service;

import com.fullstockwh.dto.request.RegisterRequest;

public interface IAuthService
{
    void registerUser(RegisterRequest registerRequest);

    String verifyUserToken(String token);
}

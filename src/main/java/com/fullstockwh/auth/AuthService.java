package com.fullstockwh.auth;

import com.fullstockwh.auth.dto.RegisterRequest;

public interface AuthService
{
    void registerUser(RegisterRequest registerRequest);

    String verifyUserToken(String token);
}

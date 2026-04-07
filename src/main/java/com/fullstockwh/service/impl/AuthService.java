package com.fullstockwh.service.impl;

import com.fullstockwh.entity.User;
import com.fullstockwh.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService
{
    private final IUserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public String Register(User user)
    {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return  "Registered Successfully!";
    }
}

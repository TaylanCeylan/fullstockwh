package com.fullstockwh.service.impl;

import com.fullstockwh.entity.User;
import com.fullstockwh.repository.UserRepository;
import com.fullstockwh.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService
{
    private final UserRepository userRepository;

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found! ID: " + id));
    }
}

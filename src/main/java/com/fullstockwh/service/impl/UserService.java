package com.fullstockwh.service.impl;

import com.fullstockwh.dto.request.UserUpdateRequest;
import com.fullstockwh.entity.UserEntity;
import com.fullstockwh.repository.UserRepository;
import com.fullstockwh.service.IUserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService
{
    private final UserRepository userRepository;

    @Override
    public UserEntity findById(Long id)
    {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found! ID: " + id));
    }

    @Override
    public UserUpdateRequest getUserUpdateRequest()
    {
        String currentEmail = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();

        var dbUser = userRepository.findByEmail(currentEmail).orElseThrow(() -> new RuntimeException("User not found! Email: " + currentEmail));

        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();

        userUpdateRequest.setFirstName(dbUser.getFirstName());
        userUpdateRequest.setLastName(dbUser.getLastName());
        userUpdateRequest.setBirthDate(dbUser.getBirthDate());
        return userUpdateRequest;
    }

    @Transactional
    @Override
    public void updateUser(UserUpdateRequest user)
    {
        String currentEmail = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();

        var dbUser = userRepository.findByEmail(currentEmail).orElseThrow(() -> new RuntimeException("User not found! Email: " + currentEmail));

        if (user.getFirstName() != null)
        {
            dbUser.setFirstName(user.getFirstName());
        }

        if (user.getLastName() != null)
        {
            dbUser.setLastName(user.getLastName());
        }

        if (user.getBirthDate() != null)
        {
            dbUser.setBirthDate(user.getBirthDate());
        }
    }
}

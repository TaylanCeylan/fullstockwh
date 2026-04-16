package com.fullstockwh.service;

import com.fullstockwh.dto.request.UserUpdateRequest;
import com.fullstockwh.entity.UserEntity;
import org.springframework.security.core.userdetails.UserDetails;

public interface IUserService
{
    UserEntity findById(Long id);

    UserUpdateRequest getUserUpdateRequest();

    void updateUser(UserUpdateRequest user);
}

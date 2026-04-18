package com.fullstockwh.user;

import com.fullstockwh.user.dto.UserUpdateRequest;

public interface UserService
{
    UserEntity findById(Long id);

    UserUpdateRequest getUserUpdateRequest();

    void updateUser(UserUpdateRequest user);
}

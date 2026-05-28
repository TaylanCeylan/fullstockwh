package com.fullstockwh.user.dto;

import com.fullstockwh.auth.enums.Role;
import lombok.Data;

@Data
public class AdminUserCreateRequest
{
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Role role;
}

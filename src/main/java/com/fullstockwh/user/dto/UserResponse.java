package com.fullstockwh.user.dto;

import com.fullstockwh.auth.enums.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse
{
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
    private boolean enabled;
}

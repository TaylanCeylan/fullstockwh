package com.fullstockwh.auth.dto;

import lombok.Data;

@Data
public class RegisterRequest
{
    private String firstName;
    private String lastName;
    @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd")
    private java.util.Date birthDate;
    private String email;
    private String password;
}

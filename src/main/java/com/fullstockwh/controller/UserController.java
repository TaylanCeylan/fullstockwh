package com.fullstockwh.controller;

import com.fullstockwh.dto.request.UserUpdateRequest;
import com.fullstockwh.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class UserController
{
    private final IUserService userService;

    @GetMapping("/user")
    public String showUserProfile(Model model)
    {
        model.addAttribute("user", userService.getUserUpdateRequest());

        return "user-profile";
    }

    @PostMapping("/user/update")
    public String updateUser(@ModelAttribute("user") UserUpdateRequest user)
    {
        userService.updateUser(user);

        return "redirect:/user?success";
    }
}

package com.fullstockwh.user;

import com.fullstockwh.user.dto.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class UserController
{
    private final UserService userService;

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

package com.fullstockwh.controller;

import com.fullstockwh.dto.request.RegisterRequest;
import com.fullstockwh.service.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AuthController
{
    private final IAuthService authService;

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model)
    {
        model.addAttribute("user", new RegisterRequest());
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(@ModelAttribute("user") RegisterRequest request, Model model)
    {
        try
        {
            authService.registerUser(request);
            return "redirect:/login?success";
        }
        catch (RuntimeException ex)
        {
            model.addAttribute("error", ex.getMessage());
            return "register";
        }
    }

    @GetMapping("/api/auth/verify")
    public String verifyEmail(@RequestParam("token") String token)
    {
        String result = authService.verifyUserToken(token);

        if (result.equals("invalid token"))
        {
            return "redirect:/login?error=invalid_token";
        }

        if (result.equals("token_expired"))
        {
            return "redirect:/login?error=token_expired";
        }

        return "redirect:/login?verified";
    }
}
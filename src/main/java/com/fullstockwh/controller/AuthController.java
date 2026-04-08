package com.fullstockwh.controller;

import com.fullstockwh.dto.request.RegisterRequest;
import com.fullstockwh.entity.User;
import com.fullstockwh.entity.VerificationToken;
import com.fullstockwh.enums.Role;
import com.fullstockwh.repository.IUserRepository;
import com.fullstockwh.repository.IVerificationTokenRepository;
import com.fullstockwh.service.IEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class AuthController
{
    private final IUserRepository userRepository;
    private final IVerificationTokenRepository tokenRepository;
    private final IEmailService emailService;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

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
        if (userRepository.findByEmail(request.getEmail()).isPresent())
        {
            model.addAttribute("error", "This email already exists");
            return "register";
        }

        User savedUser = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.CUSTOMER)
                .enabled(false)
                .build();

        userRepository.save(savedUser);

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, savedUser);
        tokenRepository.save(verificationToken);

        emailService.SendVerificationEmail(savedUser.getEmail(), verificationToken.getToken());

        return "redirect:/login?success";
    }

    @GetMapping("/api/auth/verify")
    public String verifyEmail(@RequestParam("token") String token)
    {
        Optional<VerificationToken> optionalToken = tokenRepository.findByToken(token);

        if (optionalToken.isEmpty())
        {
            return "redirect:/login?error=invalid_token";
        }

        VerificationToken verificationToken = optionalToken.get();

        if (verificationToken.getExpirationDate().isBefore(LocalDateTime.now()))
        {
            return "redirect:/login?error=token_expired";
        }

        User user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);

        return "redirect:/login?verified";
    }
}
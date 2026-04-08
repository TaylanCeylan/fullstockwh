package com.fullstockwh.controller;

import com.fullstockwh.dto.request.LoginRequest;
import com.fullstockwh.dto.request.RegisterRequest;
import com.fullstockwh.entity.User;
import com.fullstockwh.entity.VerificationToken;
import com.fullstockwh.enums.Role;
import com.fullstockwh.repository.IUserRepository;
import com.fullstockwh.repository.IVerificationTokenRepository;
import com.fullstockwh.service.impl.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.DisabledException;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController
{
    private final IUserRepository userRepository;
    private final IVerificationTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    @PostMapping("/register")
    public ResponseEntity<String> Register(@RequestBody RegisterRequest user)
    {
        if (userRepository.findByEmail(user.getEmail()).isPresent())
        {
            return ResponseEntity.badRequest().body("email already exists");
        }

        User savedUser = User.builder()
                .email(user.getEmail())
                .password(passwordEncoder.encode(user.getPassword()))
                .role(Role.CUSTOMER)
                .enabled(false)
                .build();

        userRepository.save(savedUser);

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, savedUser);
        tokenRepository.save(verificationToken);

        emailService.SendVerificationEmail(savedUser.getEmail(), verificationToken.getToken());

        return ResponseEntity.ok().body("Registered successfully. Please confirm your email");
    }

    @GetMapping("/verify")
    public ResponseEntity<String> VerifyEmail(@RequestParam("token") String token)
    {
        var optionalToken = tokenRepository.findByToken(token);

        if (optionalToken.isEmpty())
        {
            return ResponseEntity.badRequest().body("Invalid token");
        }

        VerificationToken verificationToken = optionalToken.get();

        if (verificationToken.getExpirationDate().isBefore(LocalDateTime.now()))
        {
            return ResponseEntity.badRequest().body("Token expired");
        }

        User user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);

        return ResponseEntity.ok().body("Verified successfully");
    }

    /*@PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest user, HttpServletRequest request, HttpServletResponse response)
    {
        try
        {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, request, response);

            return ResponseEntity.ok().body("logged in successfully");

        }
        catch (DisabledException e)
        {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Please confirm your email");
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email or password is incorrect");
        }
    }*/
}

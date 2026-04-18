package com.fullstockwh.auth;

import com.fullstockwh.auth.dto.RegisterRequest;
import com.fullstockwh.user.UserEntity;
import com.fullstockwh.auth.token.VerificationToken;
import com.fullstockwh.auth.enums.Role;
import com.fullstockwh.user.UserRepository;
import com.fullstockwh.auth.token.VerificationTokenRepository;
import com.fullstockwh.common.email.EmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class AuthServiceImpl implements AuthService
{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final VerificationTokenRepository verificationTokenRepository;

    @Transactional
    @Override
    public void registerUser(RegisterRequest registerRequest)
    {
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent())
        {
            throw new RuntimeException("Email already exists");
        }

        UserEntity userEntity = UserEntity.builder()
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.CUSTOMER)
                .enabled(false)
                .build();
        userRepository.save(userEntity);

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, userEntity);
        verificationTokenRepository.save(verificationToken);

        emailService.SendVerificationEmail(userEntity.getEmail(), token);
    }

    @Transactional
    @Override
    public String verifyUserToken(String token)
    {
        var optionalToken = verificationTokenRepository.findByToken(token);

        if (optionalToken.isEmpty())
        {
            return "invalid token";
        }

        VerificationToken verificationToken = optionalToken.get();

        if (verificationToken.getExpirationDate().isBefore(LocalDateTime.now()))
        {
            verificationTokenRepository.delete(verificationToken);

            return "token expired";
        }

        UserEntity userEntity = verificationToken.getUserEntity();
        userEntity.setEnabled(true);
        userRepository.save(userEntity);

        verificationTokenRepository.delete(verificationToken);

        return "verified";
    }
}

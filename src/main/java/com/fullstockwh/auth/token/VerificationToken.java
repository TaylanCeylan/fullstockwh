package com.fullstockwh.auth.token;

import com.fullstockwh.user.UserEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "verification_token")
@Data
@NoArgsConstructor
public class VerificationToken
{
    public VerificationToken(String token, UserEntity userEntity)
    {
        this.token = token;
        this.userEntity = userEntity;
        this.expirationDate = LocalDateTime.now().plusHours(24);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @OneToOne(targetEntity = UserEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private UserEntity userEntity;

    private LocalDateTime expirationDate;
}

package com.fullstockwh.user.payment_card;

import com.fullstockwh.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_card")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCard
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private String cardNickname;

    @Column(nullable = false)
    private String cardholderName;

    @Column(nullable = false, length = 4)
    private String lastFourDigits;

    @Column(nullable = false)
    private String expiryDate;

    @Column(nullable = false)
    private boolean isTemporary = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}

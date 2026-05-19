package com.fullstockwh.user.payment_card;

import com.fullstockwh.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentCardRepository extends JpaRepository<PaymentCard, Long>
{
    @Query("SELECT c FROM PaymentCard c WHERE c.user = :user AND c.isTemporary = false")
    List<PaymentCard> findByUserAndTemporaryFalse(UserEntity user);
    Optional<PaymentCard> findByIdAndUser(Long id, UserEntity user);
    boolean existsByUserAndLastFourDigitsAndExpiryDate(UserEntity user, String lastFourDigits, String expiryDate);
}

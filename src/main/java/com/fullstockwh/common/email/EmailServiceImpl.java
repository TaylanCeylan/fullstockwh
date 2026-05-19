package com.fullstockwh.common.email;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
class EmailServiceImpl implements EmailService
{
    private final JavaMailSender mailSender;

    @Override
    public void SendVerificationEmail(String email, String token)
    {
        String confirmationUrl = "http://localhost:8080/api/auth/verify?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Verification Email");
        message.setText("Please confirm your verification email " + confirmationUrl);

        mailSender.send(message);
    }
    @Override
    public void sendShippingConfirmationEmail(String email,
                                              String trackingNumber,
                                              String carrierName,
                                              LocalDateTime estimatedDelivery)
    {
        String deliveryDate = estimatedDelivery != null
                ? estimatedDelivery.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                : "-";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your Order Has Been Shipped - Fullstockwh");
        message.setText(
                "Hello,\n\n" +
                        "Your order has been handed to the carrier and is on its way.\n\n" +
                        "Carrier         : " + carrierName    + "\n" +
                        "Tracking Number : " + trackingNumber + "\n" +
                        "Estimated Date  : " + deliveryDate   + "\n\n" +
                        "You can track your shipment by logging into your account.\n\n" +
                        "Best regards,\nFullstockwh Team"
        );
        mailSender.send(message);
    }
}

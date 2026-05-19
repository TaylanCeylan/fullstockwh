package com.fullstockwh.shipment.event;

import com.fullstockwh.common.email.EmailService;
import com.fullstockwh.shipment.Shipment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShipmentEventListener
{
    private final EmailService emailService;

    @Async
    @EventListener
    public void onShipmentCreated(ShipmentCreatedEvent event)
    {
        Shipment shipment = event.getShipment();
        String   email    = shipment.getOrder().getUser().getEmail();

        try {
            emailService.sendShippingConfirmationEmail(
                    email,
                    shipment.getTrackingNumber(),
                    shipment.getCarrierName(),
                    shipment.getEstimatedDelivery()
            );
            log.info("Shipping confirmation email sent to {}", email);
        } catch (Exception e) {
            log.error("Failed to send shipping confirmation email to {}: {}", email, e.getMessage());
        }
    }
}
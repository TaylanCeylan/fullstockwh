package com.fullstockwh.shipment.event;

import com.fullstockwh.shipment.Shipment;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ShipmentCreatedEvent extends ApplicationEvent
{
    private final Shipment shipment;

    public ShipmentCreatedEvent(Object source, Shipment shipment)
    {
        super(source);
        this.shipment = shipment;
    }
}
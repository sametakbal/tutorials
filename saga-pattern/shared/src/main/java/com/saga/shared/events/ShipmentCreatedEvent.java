package com.saga.shared.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentCreatedEvent {
    private String orderId;
    private String shipmentId;
    private String estimatedDelivery;
}

package com.saga.shared.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentCancelledEvent {
    private String orderId;
    private String shipmentId;
}

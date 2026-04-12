package com.saga.shared.commands;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancelShipmentCommand {
    private String orderId;
    private String shipmentId;
}

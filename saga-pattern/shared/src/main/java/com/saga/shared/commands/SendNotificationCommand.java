package com.saga.shared.commands;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendNotificationCommand {
    private String orderId;
    private String customerEmail;
    private String type; // ORDER_CONFIRMED | ORDER_FAILED | ORDER_SHIPPED
    private String message;
}

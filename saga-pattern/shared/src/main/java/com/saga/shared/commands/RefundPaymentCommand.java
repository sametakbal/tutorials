package com.saga.shared.commands;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefundPaymentCommand {
    private String orderId;
    private String paymentId;
    private double amount;
}

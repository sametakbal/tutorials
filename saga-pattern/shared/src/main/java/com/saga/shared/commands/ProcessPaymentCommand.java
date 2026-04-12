package com.saga.shared.commands;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessPaymentCommand {
    private String orderId;
    private String customerId;
    private double amount;
}

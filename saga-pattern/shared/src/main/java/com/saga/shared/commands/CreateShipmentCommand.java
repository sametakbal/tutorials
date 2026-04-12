package com.saga.shared.commands;

import com.saga.shared.dto.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateShipmentCommand {
    private String orderId;
    private String customerId;
    private List<OrderItem> items;
}

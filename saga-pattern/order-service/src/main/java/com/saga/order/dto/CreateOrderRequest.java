package com.saga.order.dto;

import com.saga.shared.dto.OrderItem;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {
    private String customerId;
    private String customerEmail;
    private List<OrderItem> items;
}

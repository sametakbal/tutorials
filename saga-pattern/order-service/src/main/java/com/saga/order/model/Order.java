package com.saga.order.model;

import com.saga.shared.dto.OrderItem;
import com.saga.shared.dto.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Order {
    private String id;
    private String customerId;
    private String customerEmail;
    private List<OrderItem> items;
    private double totalAmount;
    private OrderStatus status;
    private String failureReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Saga adımlarından gelen referans ID'ler
    private String reservationId;
    private String paymentId;
    private String shipmentId;
}

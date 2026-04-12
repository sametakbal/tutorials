package com.saga.shared.dto;

public enum OrderStatus {
    PENDING,
    INVENTORY_RESERVED,
    PAYMENT_PROCESSED,
    SHIPMENT_CREATED,
    COMPLETED,
    COMPENSATING,
    FAILED
}

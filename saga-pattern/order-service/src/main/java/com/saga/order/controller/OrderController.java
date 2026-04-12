package com.saga.order.controller;

import com.saga.order.dto.CreateOrderRequest;
import com.saga.order.model.Order;
import com.saga.order.repository.OrderRepository;
import com.saga.order.saga.OrderSagaOrchestrator;
import com.saga.shared.dto.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderRepository orderRepository;
    private final OrderSagaOrchestrator orchestrator;

    /** Yeni sipariş oluştur ve saga'yı başlat */
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody CreateOrderRequest request) {
        if (request.getCustomerId() == null || request.getCustomerEmail() == null
                || request.getItems() == null || request.getItems().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "customerId, customerEmail ve items zorunludur"));
        }

        double totalAmount = request.getItems().stream()
            .mapToDouble(item -> item.getPrice() * item.getQuantity())
            .sum();

        Order order = new Order();
        order.setId(UUID.randomUUID().toString());
        order.setCustomerId(request.getCustomerId());
        order.setCustomerEmail(request.getCustomerEmail());
        order.setItems(request.getItems());
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        orderRepository.save(order);
        log.info("[OrderController] Yeni sipariş: {} — ₺{}", order.getId(), totalAmount);

        // Saga async olarak başlar — müşteri 202 ile anında yanıt alır
        orchestrator.start(order);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of(
            "orderId", order.getId(),
            "status", order.getStatus(),
            "totalAmount", totalAmount,
            "message", "Sipariş alındı, işleme başlandı"
        ));
    }

    /** Sipariş durumunu sorgula */
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable String id) {
        return orderRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /** Tüm siparişleri listele */
    @GetMapping
    public Collection<Order> listOrders() {
        return orderRepository.findAll();
    }
}

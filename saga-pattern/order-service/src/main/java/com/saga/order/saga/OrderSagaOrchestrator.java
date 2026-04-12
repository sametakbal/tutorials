package com.saga.order.saga;

import com.saga.order.model.Order;
import com.saga.order.repository.OrderRepository;
import com.saga.shared.commands.*;
import com.saga.shared.constants.KafkaTopics;
import com.saga.shared.dto.OrderStatus;
import com.saga.shared.events.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * OrderSagaOrchestrator — Kafka tabanlı Saga Orkestratörü.
 *
 * RabbitMQ farkı:
 *  - Exchange + routing key yok → her olay tipi kendi topic'inde yaşar
 *  - @RabbitHandler (tip tabanlı dispatch) yok → her topic için ayrı @KafkaListener metodu
 *  - Message key = orderId → aynı siparişin tüm mesajları aynı partition'a gider
 *    (partition içi sıra garantisi — compensation sırası bozulmaz)
 *
 * Başarılı akış:
 *   PENDING → Reserve Inventory → INVENTORY_RESERVED
 *           → Process Payment   → PAYMENT_PROCESSED
 *           → Create Shipment   → SHIPMENT_CREATED
 *           → Send Notification → COMPLETED
 *
 * Compensation:
 *   PAYMENT_FAILED   → Release Inventory → FAILED
 *   SHIPMENT_FAILED  → Refund Payment → Release Inventory → FAILED
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderSagaOrchestrator {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OrderRepository orderRepository;

    // ─── Saga Başlatma ─────────────────────────────────────────────────────────

    public void start(Order order) {
        log(order.getId(), "Saga başlatıldı → stok rezervasyonu isteniyor");
        send(KafkaTopics.CMD_RESERVE_INVENTORY, order.getId(),
            new ReserveInventoryCommand(order.getId(), order.getItems()));
    }

    // ─── Başarılı Adımlar ──────────────────────────────────────────────────────

    @KafkaListener(topics = KafkaTopics.EVT_INVENTORY_RESERVED, groupId = "order-saga-group")
    public void onInventoryReserved(InventoryReservedEvent event) {
        Order order = getOrder(event.getOrderId());
        order.setStatus(OrderStatus.INVENTORY_RESERVED);
        order.setReservationId(event.getReservationId());
        order.setUpdatedAt(LocalDateTime.now());

        log(order.getId(), "Stok rezerve edildi (%s) → ödeme işleniyor"
            .formatted(event.getReservationId()));

        send(KafkaTopics.CMD_PROCESS_PAYMENT, order.getId(),
            new ProcessPaymentCommand(order.getId(), order.getCustomerId(), order.getTotalAmount()));
    }

    @KafkaListener(topics = KafkaTopics.EVT_PAYMENT_PROCESSED, groupId = "order-saga-group")
    public void onPaymentProcessed(PaymentProcessedEvent event) {
        Order order = getOrder(event.getOrderId());
        order.setStatus(OrderStatus.PAYMENT_PROCESSED);
        order.setPaymentId(event.getPaymentId());
        order.setUpdatedAt(LocalDateTime.now());

        log(order.getId(), "Ödeme alındı (%s) → kargo oluşturuluyor"
            .formatted(event.getPaymentId()));

        send(KafkaTopics.CMD_CREATE_SHIPMENT, order.getId(),
            new CreateShipmentCommand(order.getId(), order.getCustomerId(), order.getItems()));
    }

    @KafkaListener(topics = KafkaTopics.EVT_SHIPMENT_CREATED, groupId = "order-saga-group")
    public void onShipmentCreated(ShipmentCreatedEvent event) {
        Order order = getOrder(event.getOrderId());
        order.setStatus(OrderStatus.SHIPMENT_CREATED);
        order.setShipmentId(event.getShipmentId());
        order.setUpdatedAt(LocalDateTime.now());

        log(order.getId(), "Kargo oluşturuldu (%s) → bildirim gönderiliyor"
            .formatted(event.getShipmentId()));

        send(KafkaTopics.CMD_SEND_NOTIFICATION, order.getId(),
            new SendNotificationCommand(
                order.getId(), order.getCustomerEmail(), "ORDER_CONFIRMED",
                "Siparişiniz onaylandı! Kargo no: %s. Tahmini teslimat: %s"
                    .formatted(event.getShipmentId(), event.getEstimatedDelivery())
            ));
    }

    @KafkaListener(topics = KafkaTopics.EVT_NOTIFICATION_SENT, groupId = "order-saga-group")
    public void onNotificationSent(NotificationSentEvent event) {
        Order order = getOrder(event.getOrderId());
        order.setStatus(OrderStatus.COMPLETED);
        order.setUpdatedAt(LocalDateTime.now());

        log(order.getId(), "✅ Saga tamamlandı — sipariş COMPLETED");
    }

    // ─── Compensating Transaction'lar ─────────────────────────────────────────

    @KafkaListener(topics = KafkaTopics.EVT_INVENTORY_RESERVATION_FAILED, groupId = "order-saga-group")
    public void onInventoryReservationFailed(InventoryReservationFailedEvent event) {
        Order order = getOrder(event.getOrderId());
        order.setStatus(OrderStatus.FAILED);
        order.setFailureReason("Stok yetersiz: " + event.getReason());
        order.setUpdatedAt(LocalDateTime.now());

        log(order.getId(), "❌ Stok rezervasyonu başarısız — Saga FAILED (%s)"
            .formatted(event.getReason()));

        sendFailureNotification(order);
    }

    @KafkaListener(topics = KafkaTopics.EVT_PAYMENT_FAILED, groupId = "order-saga-group")
    public void onPaymentFailed(PaymentFailedEvent event) {
        Order order = getOrder(event.getOrderId());
        order.setStatus(OrderStatus.COMPENSATING);
        order.setFailureReason("Ödeme başarısız: " + event.getReason());
        order.setUpdatedAt(LocalDateTime.now());

        log(order.getId(), "⚠️  Ödeme başarısız → Compensation: stok serbest bırakılıyor");

        send(KafkaTopics.CMD_RELEASE_INVENTORY, order.getId(),
            new ReleaseInventoryCommand(order.getId(), order.getItems()));
    }

    @KafkaListener(topics = KafkaTopics.EVT_INVENTORY_RELEASED, groupId = "order-saga-group")
    public void onInventoryReleased(InventoryReleasedEvent event) {
        Order order = getOrder(event.getOrderId());
        order.setStatus(OrderStatus.FAILED);
        order.setUpdatedAt(LocalDateTime.now());

        log(order.getId(), "❌ Stok serbest bırakıldı — Saga FAILED");

        sendFailureNotification(order);
    }

    @KafkaListener(topics = KafkaTopics.EVT_SHIPMENT_FAILED, groupId = "order-saga-group")
    public void onShipmentFailed(ShipmentFailedEvent event) {
        Order order = getOrder(event.getOrderId());
        order.setStatus(OrderStatus.COMPENSATING);
        order.setFailureReason("Kargo başarısız: " + event.getReason());
        order.setUpdatedAt(LocalDateTime.now());

        log(order.getId(), "⚠️  Kargo başarısız → Compensation: ödeme iade ediliyor");

        send(KafkaTopics.CMD_REFUND_PAYMENT, order.getId(),
            new RefundPaymentCommand(order.getId(), order.getPaymentId(), order.getTotalAmount()));
    }

    @KafkaListener(topics = KafkaTopics.EVT_PAYMENT_REFUNDED, groupId = "order-saga-group")
    public void onPaymentRefunded(PaymentRefundedEvent event) {
        Order order = getOrder(event.getOrderId());
        log(order.getId(), "Ödeme iade edildi → stok serbest bırakılıyor");

        send(KafkaTopics.CMD_RELEASE_INVENTORY, order.getId(),
            new ReleaseInventoryCommand(order.getId(), order.getItems()));
    }

    @KafkaListener(topics = KafkaTopics.EVT_SHIPMENT_CANCELLED, groupId = "order-saga-group")
    public void onShipmentCancelled(ShipmentCancelledEvent event) {
        log(event.getOrderId(), "Kargo iptal onayı alındı (%s)".formatted(event.getShipmentId()));
    }

    // ─── Yardımcılar ──────────────────────────────────────────────────────────

    /**
     * orderId'yi Kafka message key olarak kullanır.
     * Aynı siparişe ait tüm mesajlar aynı partition'a yönlendirilir.
     * Bu sayede partition içi sıra garantisi: compensation adımları doğru sırada işlenir.
     */
    private void send(String topic, String orderId, Object payload) {
        kafkaTemplate.send(topic, orderId, payload);
        log.debug("  → [KOMUT] topic={} key={}", topic, orderId);
    }

    private void sendFailureNotification(Order order) {
        send(KafkaTopics.CMD_SEND_NOTIFICATION, order.getId(),
            new SendNotificationCommand(
                order.getId(), order.getCustomerEmail(), "ORDER_FAILED",
                "Siparişiniz işlenemedi. Sebep: " + order.getFailureReason()
            ));
    }

    private Order getOrder(String orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalStateException("Sipariş bulunamadı: " + orderId));
    }

    private void log(String orderId, String message) {
        log.info("[Saga][{}] {}", orderId.substring(0, Math.min(8, orderId.length())), message);
    }
}

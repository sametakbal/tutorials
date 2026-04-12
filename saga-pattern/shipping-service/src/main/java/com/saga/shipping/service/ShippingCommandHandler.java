package com.saga.shipping.service;

import com.saga.shared.commands.CancelShipmentCommand;
import com.saga.shared.commands.CreateShipmentCommand;
import com.saga.shared.constants.KafkaTopics;
import com.saga.shared.events.ShipmentCancelledEvent;
import com.saga.shared.events.ShipmentCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShippingCommandHandler {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Map<String, String> shipmentStatus = new ConcurrentHashMap<>();
    private final Random random = new Random();

    @KafkaListener(topics = KafkaTopics.CMD_CREATE_SHIPMENT, groupId = "shipping-service-group")
    public void handleCreateShipment(CreateShipmentCommand cmd) throws InterruptedException {
        log.info("[Shipping] Kargo oluşturuluyor — sipariş: {}", cmd.getOrderId());
        Thread.sleep(400);

        String shipmentId = UUID.randomUUID().toString();
        String estimatedDelivery = LocalDate.now().plusDays(3 + random.nextInt(3)).toString();
        shipmentStatus.put(shipmentId, "CREATED");

        log.info("[Shipping] ✅ Kargo oluşturuldu — shipment: {}", shipmentId);
        publish(KafkaTopics.EVT_SHIPMENT_CREATED, cmd.getOrderId(),
            new ShipmentCreatedEvent(cmd.getOrderId(), shipmentId, estimatedDelivery));
    }

    @KafkaListener(topics = KafkaTopics.CMD_CANCEL_SHIPMENT, groupId = "shipping-service-group")
    public void handleCancelShipment(CancelShipmentCommand cmd) {
        log.info("[Shipping] Kargo iptal ediliyor — shipment: {} (Compensation)", cmd.getShipmentId());
        shipmentStatus.put(cmd.getShipmentId(), "CANCELLED");

        log.info("[Shipping] ✅ Kargo iptal edildi");
        publish(KafkaTopics.EVT_SHIPMENT_CANCELLED, cmd.getOrderId(),
            new ShipmentCancelledEvent(cmd.getOrderId(), cmd.getShipmentId()));
    }

    private void publish(String topic, String key, Object event) {
        kafkaTemplate.send(topic, key, event);
        log.debug("  ← [OLAY] topic={} key={}", topic, key);
    }
}

package com.saga.inventory.service;

import com.saga.shared.commands.ReleaseInventoryCommand;
import com.saga.shared.commands.ReserveInventoryCommand;
import com.saga.shared.constants.KafkaTopics;
import com.saga.shared.dto.OrderItem;
import com.saga.shared.events.InventoryReleasedEvent;
import com.saga.shared.events.InventoryReservationFailedEvent;
import com.saga.shared.events.InventoryReservedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryCommandHandler {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final Map<String, int[]> stock = new ConcurrentHashMap<>(Map.of(
        "P001", new int[]{10, 0},
        "P002", new int[]{50, 0},
        "P003", new int[]{30, 0},
        "P004", new int[]{5,  0},
        "P005", new int[]{0,  0}  // Stokta yok — başarısız senaryo için
    ));

    private final Map<String, String> productNames = Map.of(
        "P001", "Laptop", "P002", "Klavye", "P003", "Mouse",
        "P004", "Monitör", "P005", "Kulaklık"
    );

    private final Map<String, List<OrderItem>> reservations = new ConcurrentHashMap<>();

    @KafkaListener(topics = KafkaTopics.CMD_RESERVE_INVENTORY, groupId = "inventory-service-group")
    public void handleReserveInventory(ReserveInventoryCommand cmd) {
        log.info("[Inventory] Stok rezervasyonu istendi — sipariş: {}", cmd.getOrderId());

        for (OrderItem item : cmd.getItems()) {
            int[] counts = stock.get(item.getProductId());
            if (counts == null || counts[0] < item.getQuantity()) {
                String name = productNames.getOrDefault(item.getProductId(), item.getProductId());
                String reason = counts == null
                    ? "Ürün bulunamadı: " + item.getProductId()
                    : "Yetersiz stok: %s (mevcut: %d, istenen: %d)"
                        .formatted(name, counts[0], item.getQuantity());

                log.warn("[Inventory] ❌ Rezervasyon başarısız — {}", reason);
                publish(KafkaTopics.EVT_INVENTORY_RESERVATION_FAILED, cmd.getOrderId(),
                    new InventoryReservationFailedEvent(cmd.getOrderId(), reason));
                return;
            }
        }

        for (OrderItem item : cmd.getItems()) {
            int[] counts = stock.get(item.getProductId());
            counts[0] -= item.getQuantity();
            counts[1] += item.getQuantity();
        }

        String reservationId = UUID.randomUUID().toString();
        reservations.put(cmd.getOrderId(), cmd.getItems());

        log.info("[Inventory] ✅ Stok rezerve edildi — rezervasyon: {}", reservationId);
        publish(KafkaTopics.EVT_INVENTORY_RESERVED, cmd.getOrderId(),
            new InventoryReservedEvent(cmd.getOrderId(), reservationId));
    }

    @KafkaListener(topics = KafkaTopics.CMD_RELEASE_INVENTORY, groupId = "inventory-service-group")
    public void handleReleaseInventory(ReleaseInventoryCommand cmd) {
        log.info("[Inventory] Stok serbest bırakılıyor — sipariş: {} (Compensation)", cmd.getOrderId());

        List<OrderItem> items = reservations.getOrDefault(cmd.getOrderId(), cmd.getItems());
        for (OrderItem item : items) {
            int[] counts = stock.get(item.getProductId());
            if (counts != null) {
                counts[0] += item.getQuantity();
                counts[1] = Math.max(0, counts[1] - item.getQuantity());
            }
        }
        reservations.remove(cmd.getOrderId());

        log.info("[Inventory] ✅ Stok serbest bırakıldı — compensation tamamlandı");
        publish(KafkaTopics.EVT_INVENTORY_RELEASED, cmd.getOrderId(),
            new InventoryReleasedEvent(cmd.getOrderId()));
    }

    private void publish(String topic, String key, Object event) {
        kafkaTemplate.send(topic, key, event);
        log.debug("  ← [OLAY] topic={} key={}", topic, key);
    }
}

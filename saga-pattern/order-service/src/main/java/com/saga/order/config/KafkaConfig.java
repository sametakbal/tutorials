package com.saga.order.config;

import com.saga.shared.constants.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Order service'in kullandığı tüm Kafka topic'lerini tanımlar.
 * Spring'in KafkaAdmin bean'i bu tanımları Kafka broker'da otomatik oluşturur.
 * Partitions=3: orderId key'i ile aynı sipariş mesajları aynı partition'a gider.
 */
@Configuration
public class KafkaConfig {

    // ─── Komut Topic'leri (yayınlanır) ────────────────────────────────────────
    @Bean public NewTopic reserveInventoryTopic()  { return topic(KafkaTopics.CMD_RESERVE_INVENTORY); }
    @Bean public NewTopic releaseInventoryTopic()  { return topic(KafkaTopics.CMD_RELEASE_INVENTORY); }
    @Bean public NewTopic processPaymentTopic()    { return topic(KafkaTopics.CMD_PROCESS_PAYMENT); }
    @Bean public NewTopic refundPaymentTopic()     { return topic(KafkaTopics.CMD_REFUND_PAYMENT); }
    @Bean public NewTopic createShipmentTopic()    { return topic(KafkaTopics.CMD_CREATE_SHIPMENT); }
    @Bean public NewTopic cancelShipmentTopic()    { return topic(KafkaTopics.CMD_CANCEL_SHIPMENT); }
    @Bean public NewTopic sendNotificationTopic()  { return topic(KafkaTopics.CMD_SEND_NOTIFICATION); }

    // ─── Olay Topic'leri (tüketilir) ──────────────────────────────────────────
    @Bean public NewTopic inventoryReservedTopic()           { return topic(KafkaTopics.EVT_INVENTORY_RESERVED); }
    @Bean public NewTopic inventoryReleasedTopic()           { return topic(KafkaTopics.EVT_INVENTORY_RELEASED); }
    @Bean public NewTopic inventoryReservationFailedTopic()  { return topic(KafkaTopics.EVT_INVENTORY_RESERVATION_FAILED); }
    @Bean public NewTopic paymentProcessedTopic()            { return topic(KafkaTopics.EVT_PAYMENT_PROCESSED); }
    @Bean public NewTopic paymentRefundedTopic()             { return topic(KafkaTopics.EVT_PAYMENT_REFUNDED); }
    @Bean public NewTopic paymentFailedTopic()               { return topic(KafkaTopics.EVT_PAYMENT_FAILED); }
    @Bean public NewTopic shipmentCreatedTopic()             { return topic(KafkaTopics.EVT_SHIPMENT_CREATED); }
    @Bean public NewTopic shipmentCancelledTopic()           { return topic(KafkaTopics.EVT_SHIPMENT_CANCELLED); }
    @Bean public NewTopic shipmentFailedTopic()              { return topic(KafkaTopics.EVT_SHIPMENT_FAILED); }
    @Bean public NewTopic notificationSentTopic()            { return topic(KafkaTopics.EVT_NOTIFICATION_SENT); }

    private NewTopic topic(String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }
}

package com.saga.inventory.config;

import com.saga.shared.constants.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean public NewTopic reserveInventoryTopic() { return topic(KafkaTopics.CMD_RESERVE_INVENTORY); }
    @Bean public NewTopic releaseInventoryTopic() { return topic(KafkaTopics.CMD_RELEASE_INVENTORY); }
    @Bean public NewTopic inventoryReservedTopic()          { return topic(KafkaTopics.EVT_INVENTORY_RESERVED); }
    @Bean public NewTopic inventoryReleasedTopic()          { return topic(KafkaTopics.EVT_INVENTORY_RELEASED); }
    @Bean public NewTopic inventoryReservationFailedTopic() { return topic(KafkaTopics.EVT_INVENTORY_RESERVATION_FAILED); }

    private NewTopic topic(String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }
}

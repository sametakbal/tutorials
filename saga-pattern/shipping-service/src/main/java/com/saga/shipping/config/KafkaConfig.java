package com.saga.shipping.config;

import com.saga.shared.constants.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean public NewTopic createShipmentTopic()   { return topic(KafkaTopics.CMD_CREATE_SHIPMENT); }
    @Bean public NewTopic cancelShipmentTopic()   { return topic(KafkaTopics.CMD_CANCEL_SHIPMENT); }
    @Bean public NewTopic shipmentCreatedTopic()  { return topic(KafkaTopics.EVT_SHIPMENT_CREATED); }
    @Bean public NewTopic shipmentCancelledTopic(){ return topic(KafkaTopics.EVT_SHIPMENT_CANCELLED); }
    @Bean public NewTopic shipmentFailedTopic()   { return topic(KafkaTopics.EVT_SHIPMENT_FAILED); }

    private NewTopic topic(String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }
}

package com.saga.notification.config;

import com.saga.shared.constants.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean public NewTopic sendNotificationTopic() { return topic(KafkaTopics.CMD_SEND_NOTIFICATION); }
    @Bean public NewTopic notificationSentTopic() { return topic(KafkaTopics.EVT_NOTIFICATION_SENT); }

    private NewTopic topic(String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }
}

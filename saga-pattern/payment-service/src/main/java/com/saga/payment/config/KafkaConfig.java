package com.saga.payment.config;

import com.saga.shared.constants.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean public NewTopic processPaymentTopic()  { return topic(KafkaTopics.CMD_PROCESS_PAYMENT); }
    @Bean public NewTopic refundPaymentTopic()   { return topic(KafkaTopics.CMD_REFUND_PAYMENT); }
    @Bean public NewTopic paymentProcessedTopic() { return topic(KafkaTopics.EVT_PAYMENT_PROCESSED); }
    @Bean public NewTopic paymentRefundedTopic()  { return topic(KafkaTopics.EVT_PAYMENT_REFUNDED); }
    @Bean public NewTopic paymentFailedTopic()    { return topic(KafkaTopics.EVT_PAYMENT_FAILED); }

    private NewTopic topic(String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }
}

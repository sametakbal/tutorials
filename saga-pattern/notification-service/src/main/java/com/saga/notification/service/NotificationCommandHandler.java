package com.saga.notification.service;

import com.saga.shared.commands.SendNotificationCommand;
import com.saga.shared.constants.KafkaTopics;
import com.saga.shared.events.NotificationSentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationCommandHandler {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = KafkaTopics.CMD_SEND_NOTIFICATION, groupId = "notification-service-group")
    public void handleSendNotification(SendNotificationCommand cmd) throws InterruptedException {
        String icon = "ORDER_CONFIRMED".equals(cmd.getType()) ? "📧" : "❌";
        log.info("[Notification] {} E-posta gönderiliyor → {}", icon, cmd.getCustomerEmail());
        log.info("[Notification]   Tür   : {}", cmd.getType());
        log.info("[Notification]   Mesaj : {}", cmd.getMessage());

        Thread.sleep(200);

        log.info("[Notification] ✅ Bildirim gönderildi");
        kafkaTemplate.send(KafkaTopics.EVT_NOTIFICATION_SENT, cmd.getOrderId(),
            new NotificationSentEvent(cmd.getOrderId(), cmd.getType()));
    }
}

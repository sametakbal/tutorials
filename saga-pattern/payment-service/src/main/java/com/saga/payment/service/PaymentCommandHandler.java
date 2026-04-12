package com.saga.payment.service;

import com.saga.shared.commands.ProcessPaymentCommand;
import com.saga.shared.commands.RefundPaymentCommand;
import com.saga.shared.constants.KafkaTopics;
import com.saga.shared.events.PaymentFailedEvent;
import com.saga.shared.events.PaymentProcessedEvent;
import com.saga.shared.events.PaymentRefundedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentCommandHandler {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${payment.failure-rate:0.2}")
    private double failureRate;

    private final Map<String, Double> payments = new ConcurrentHashMap<>();

    @KafkaListener(topics = KafkaTopics.CMD_PROCESS_PAYMENT, groupId = "payment-service-group")
    public void handleProcessPayment(ProcessPaymentCommand cmd) throws InterruptedException {
        log.info("[Payment] Ödeme işleniyor — sipariş: {}, tutar: ₺{}", cmd.getOrderId(), cmd.getAmount());
        Thread.sleep(500);

        if (Math.random() < failureRate) {
            log.warn("[Payment] ❌ Ödeme reddedildi — kart yetersiz bakiye");
            publish(KafkaTopics.EVT_PAYMENT_FAILED, cmd.getOrderId(),
                new PaymentFailedEvent(cmd.getOrderId(), "Kart reddi — yetersiz bakiye"));
            return;
        }

        String paymentId = UUID.randomUUID().toString();
        payments.put(paymentId, cmd.getAmount());

        log.info("[Payment] ✅ Ödeme alındı — payment: {}", paymentId);
        publish(KafkaTopics.EVT_PAYMENT_PROCESSED, cmd.getOrderId(),
            new PaymentProcessedEvent(cmd.getOrderId(), paymentId, cmd.getAmount()));
    }

    @KafkaListener(topics = KafkaTopics.CMD_REFUND_PAYMENT, groupId = "payment-service-group")
    public void handleRefundPayment(RefundPaymentCommand cmd) throws InterruptedException {
        log.info("[Payment] İade işleniyor — sipariş: {}, payment: {} (Compensation)",
            cmd.getOrderId(), cmd.getPaymentId());
        Thread.sleep(300);

        payments.remove(cmd.getPaymentId());

        log.info("[Payment] ✅ İade tamamlandı — ₺{}", cmd.getAmount());
        publish(KafkaTopics.EVT_PAYMENT_REFUNDED, cmd.getOrderId(),
            new PaymentRefundedEvent(cmd.getOrderId(), cmd.getPaymentId()));
    }

    private void publish(String topic, String key, Object event) {
        kafkaTemplate.send(topic, key, event);
        log.debug("  ← [OLAY] topic={} key={}", topic, key);
    }
}

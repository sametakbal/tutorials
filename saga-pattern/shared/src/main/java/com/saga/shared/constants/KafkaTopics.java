package com.saga.shared.constants;

/**
 * Tüm Kafka topic isimleri merkezi olarak burada tanımlanır.
 * Tasarım kararı:
 *  - Komutlar: her komut tipi için ayrı topic (tip güvenliği, net yönlendirme)
 *  - Olaylar:  her olay tipi için ayrı topic (orkestratör her birine subscribe olur)
 * Message key olarak orderId kullanılır → aynı siparişin tüm
 * mesajları aynı partition'a gider → partition içi sıra garantisi sağlanır.
 */
public final class KafkaTopics {

    private KafkaTopics() {}

    // ─── Komut Topic'leri (Orkestratör → Servisler) ────────────────────────────
    public static final String CMD_RESERVE_INVENTORY = "inventory.reserve";
    public static final String CMD_RELEASE_INVENTORY = "inventory.release";
    public static final String CMD_PROCESS_PAYMENT   = "payment.process";
    public static final String CMD_REFUND_PAYMENT    = "payment.refund";
    public static final String CMD_CREATE_SHIPMENT   = "shipping.create";
    public static final String CMD_CANCEL_SHIPMENT   = "shipping.cancel";
    public static final String CMD_SEND_NOTIFICATION = "notification.send";

    // ─── Olay Topic'leri (Servisler → Orkestratör) ─────────────────────────────
    public static final String EVT_INVENTORY_RESERVED           = "inventory.reserved";
    public static final String EVT_INVENTORY_RELEASED           = "inventory.released";
    public static final String EVT_INVENTORY_RESERVATION_FAILED = "inventory.reservation.failed";
    public static final String EVT_PAYMENT_PROCESSED            = "payment.processed";
    public static final String EVT_PAYMENT_REFUNDED             = "payment.refunded";
    public static final String EVT_PAYMENT_FAILED               = "payment.failed";
    public static final String EVT_SHIPMENT_CREATED             = "shipping.created";
    public static final String EVT_SHIPMENT_CANCELLED           = "shipping.cancelled";
    public static final String EVT_SHIPMENT_FAILED              = "shipping.failed";
    public static final String EVT_NOTIFICATION_SENT            = "notification.sent";
}

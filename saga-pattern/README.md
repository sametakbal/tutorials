# Saga Pattern — E-Ticaret Mikroservis Örneği

> **Spring Boot 3** · **Java 21** · **Apache Kafka** · **Maven Multi-Module**

---

## İçindekiler

1. [Problem: Dağıtık İşlemler](#1-problem-dağıtık-i̇şlemler)
2. [Çözüm: Saga Pattern](#2-çözüm-saga-pattern)
3. [Orkestrasyon mu, Koreografi mi?](#3-orkestrasyon-mu-koreografi-mi)
4. [Bu Projenin Mimarisi](#4-bu-projenin-mimarisi)
5. [Saga Akış Diyagramları](#5-saga-akış-diyagramları)
6. [Compensating Transaction Mantığı](#6-compensating-transaction-mantığı)
7. [Teknik Kararlar ve Gerekçeleri](#7-teknik-kararlar-ve-gerekçeleri)
8. [Proje Yapısı](#8-proje-yapısı)
9. [Çalıştırma](#9-çalıştırma)
10. [Test Senaryoları](#10-test-senaryoları)

---

## 1. Problem: Dağıtık İşlemler

### Tek Servis Dünyasında Hayat Kolaydı

Monolitik bir uygulamada sipariş vermek şu şekilde çalışıyordu:

```
BEGIN TRANSACTION
  INSERT INTO orders       → Sipariş oluştur
  UPDATE inventory         → Stok düş
  INSERT INTO payments     → Ödeme al
  INSERT INTO shipments    → Kargo oluştur
COMMIT  ✅  (ya da hepsi geri alınır)
ROLLBACK ❌
```

Veritabanının **ACID** garantisi sayesinde ya hepsi olurdu ya hiçbiri.

---

### Mikroservise Geçince Ne Değişti?

Her servis **kendi veritabanını** yönetir. Artık tek bir `COMMIT` komutuyla her şeyi atomik yapamayız:

```
Order Service DB     →  Sipariş oluşturuldu  ✅
Inventory Service DB →  Stok rezerve edildi  ✅
Payment Service DB   →  Ödeme alındı         ✅
Shipping Service DB  →  Kargo oluşturuldu    ❌  ← Burada çöktü!
```

**Sonuç:** Müşteriden para çekildi, stok düşürüldü ama kargo oluşturulamadı.  
Veri **tutarsız** hâle geldi. Klasik `ROLLBACK` çalışmaz çünkü farklı veritabanları var.

---

### Neden 2PC (Two-Phase Commit) Çözüm Değil?

İki aşamalı commit protokolü teorik olarak bu sorunu çözer ama pratikte:

| Sorun | Açıklama |
|---|---|
| **Blokaj** | Koordinatör çöktüğünde tüm servisler kilitlenir |
| **Yavaşlık** | Her işlemde tüm servislerin onayı beklenir |
| **Bağımlılık** | Servisler birbirinden ayrılamaz hâle gelir |
| **Destek yok** | NoSQL, mesaj kuyrukları 2PC desteklemez |

---

## 2. Çözüm: Saga Pattern

**Saga Pattern**, büyük bir dağıtık işlemi, her biri kendi veritabanında çalışan **küçük yerel işlemlere** böler.

### Temel Fikir

```
Büyük İşlem  =  Yerel İşlem 1  +  Yerel İşlem 2  +  ...  +  Yerel İşlem N
```

Her adım başarısız olursa, önceki adımların etkisini geri almak için **compensating transaction** (telafi işlemi) çalışır.

### Neden "Saga"?

1987 yılında Hector Garcia-Molina ve Kenneth Salem'in makalesinden alınmıştır. Uzun süren veritabanı işlemlerini daha küçük parçalara bölme fikri o dönemde tek bir DB için tasarlanmış, mikroservis çağında yeniden popülerleşmiştir.

---

### Saga'nın ACID ile Karşılaştırması

| Özellik | ACID | Saga |
|---|---|---|
| **Atomiklik** | Tam garanti | Telafi ile sağlanır |
| **Tutarlılık** | Anlık | Nihai (Eventually Consistent) |
| **İzolasyon** | Tam izolasyon | Kısmi (ara durumlar görünür) |
| **Dayanıklılık** | ✅ | ✅ |

> **Önemli:** Saga ile birlikte "nihai tutarlılık" (eventual consistency) kabul edilir.  
> Bir an için tutarsız görünen veri, tüm adımlar tamamlandığında tutarlı hâle gelir.

---

## 3. Orkestrasyon mu, Koreografi mi?

Saga iki farklı şekilde uygulanabilir:

### Koreografi (Choreography)

Merkezi bir koordinatör yoktur. Her servis olayları dinler ve tepki verir:

```
Order Service  →─ "OrderCreated" ────────────→  Inventory Service
                                                        ↓
               ←─ "InventoryReserved" ──────────────────
                                                        ↓
               ─────────────────────────────→  Payment Service
                                                        ↓
               ←─ "PaymentProcessed" ───────────────────
```

**Avantajı:** Servisler birbirinden tamamen bağımsız.  
**Dezavantajı:** Akış dağınık, debug edilmesi zor, döngüsel bağımlılık riski.

---

### Orkestrasyon (Orchestration) ← Bu projede kullanılan

Merkezi bir **orkestratör** tüm adımları yönetir:

```
                    ┌─────────────────────┐
                    │   ORDER SERVICE     │
                    │  (Saga Orchestrator)│
                    └─────────┬───────────┘
              ┌───────────────┼───────────────┐
              ▼               ▼               ▼
       Inventory         Payment          Shipping
        Service           Service          Service
```

**Avantajı:** Akış merkezde, anlaşılması kolay, durum takibi basit.  
**Dezavantajı:** Orkestratör tek hata noktası olabilir (ölçeklendirme ile çözülür).

---

### Hangi Durumlarda Ne Kullanılmalı?

| Senaryo | Öneri |
|---|---|
| 2-3 servis, basit akış | Koreografi |
| Karmaşık iş kuralları, koşullu adımlar | Orkestrasyon |
| Adım sayısı 5+ | Orkestrasyon |
| İzlenebilirlik kritik | Orkestrasyon |
| Servis özerkliği öncelik | Koreografi |

---

## 4. Bu Projenin Mimarisi

### Servisler ve Sorumlulukları

```
┌─────────────────────────────────────────────────────────────┐
│                      KULLANICI                              │
│              POST /orders  →  GET /orders/{id}              │
└───────────────────────────┬─────────────────────────────────┘
                            │ HTTP
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                   ORDER SERVICE  :8081                      │
│                                                             │
│   OrderController  →  OrderSagaOrchestrator                │
│                              │                              │
│                    RabbitTemplate (komut yayınla)           │
│                    @RabbitListener (olay dinle)             │
└───────────────────────────┬─────────────────────────────────┘
                            │
              ──────────────┼──────────────
             │         RabbitMQ           │
             │    saga.commands exchange   │
             │    saga.events exchange     │
              ──────────────┼──────────────
                            │
        ┌───────────────────┼───────────────────┐
        ▼                   ▼                   ▼
┌──────────────┐   ┌──────────────┐   ┌──────────────────┐
│  INVENTORY   │   │   PAYMENT    │   │    SHIPPING      │
│  SERVICE     │   │   SERVICE    │   │    SERVICE       │
│  :8082       │   │   :8083      │   │    :8084         │
│              │   │              │   │                  │
│  Stok        │   │  Ödeme al    │   │  Kargo           │
│  rezerve et  │   │  İade et     │   │  oluştur/iptal   │
│  Serbest     │   │  (%20 hata)  │   │                  │
│  bırak       │   │              │   │                  │
└──────────────┘   └──────────────┘   └──────────────────┘
                                              │
                              ┌───────────────┘
                              ▼
                    ┌──────────────────┐
                    │  NOTIFICATION    │
                    │  SERVICE  :8085  │
                    │                  │
                    │  E-posta gönder  │
                    │  (log'a yazar)   │
                    └──────────────────┘
```

### Mesaj Akışı

```
Orkestratör                   Kafka Broker                  Servisler
     │                             │                             │
     │── send(topic, key, cmd) ──► │ ── topic + partition ─────► │
     │                             │                             │
     │  ◄──── @KafkaListener ───── │ ◄──── topic + partition ─── │
```

| Yön | Topic | Üretici | Tüketici |
|---|---|---|---|
| Komut | `inventory.reserve` | Order | Inventory |
| Komut | `payment.process` | Order | Payment |
| Komut | `shipping.create` | Order | Shipping |
| Komut | `notification.send` | Order | Notification |
| Olay | `inventory.reserved` | Inventory | Order |
| Olay | `payment.failed` | Payment | Order |
| … | … | … | … |

**Message Key = orderId** → aynı siparişin tüm mesajları aynı partition'a gider → partition içi sıra garantisi sağlanır.

---

## 5. Saga Akış Diyagramları

### ✅ Başarılı Senaryo

```
Kullanıcı        Order Svc        Inventory        Payment          Shipping      Notification
    │                │                │               │                │               │
    │─ POST /orders ─►                │               │                │               │
    │                │                │               │                │               │
    │                │ RESERVE_INV ──►│               │                │               │
    │                │                │               │                │               │
    │                │◄── INV_RESERVED│               │                │               │
    │                │  status=INVENTORY_RESERVED      │                │               │
    │                │                │               │                │               │
    │                │ PROCESS_PAY ───────────────────►               │               │
    │                │                │               │                │               │
    │                │◄─────────────── PAY_PROCESSED  │               │               │
    │                │  status=PAYMENT_PROCESSED       │               │               │
    │                │                │               │                │               │
    │                │ CREATE_SHIP ───────────────────────────────────►               │
    │                │                │               │                │               │
    │                │◄──────────────────────────────── SHIP_CREATED   │               │
    │                │  status=SHIPMENT_CREATED        │               │               │
    │                │                │               │                │               │
    │                │ SEND_NOTIF ────────────────────────────────────────────────────►
    │                │                │               │                │               │
    │                │◄──────────────────────────────────────────────── NOTIF_SENT     │
    │                │  status=COMPLETED               │               │               │
    │                │                │               │                │               │
    │◄─ 202 Accepted─│                │               │                │               │
```

---

### ❌ Ödeme Başarısız — Compensation Senaryosu

```
Kullanıcı        Order Svc        Inventory        Payment          Shipping
    │                │                │               │                │
    │─ POST /orders ─►                │               │                │
    │                │                │               │                │
    │                │ RESERVE_INV ──►│               │                │
    │                │◄── INV_RESERVED│               │                │
    │                │  status=INVENTORY_RESERVED      │                │
    │                │                │               │                │
    │                │ PROCESS_PAY ───────────────────►               │
    │                │                │               │                │
    │                │◄─────────────── PAY_FAILED      │               │
    │                │  status=COMPENSATING            │               │
    │                │                │               │                │
    │     ◄──── Compensation Başlıyor ────────────────────────────────┘
    │                │                │               │
    │                │ RELEASE_INV ──►│               │
    │                │◄── INV_RELEASED│               │
    │                │  status=FAILED │               │
    │                │                │               │
    │                │ SEND_NOTIF (ORDER_FAILED) ──────────────────────►
```

---

### ❌ Kargo Başarısız — Zincirleme Compensation

```
          Order Svc        Inventory        Payment          Shipping
               │                │               │                │
               │ ... (stok ve ödeme başarılı) ...               │
               │                │               │                │
               │ CREATE_SHIP ───────────────────────────────────►
               │                │               │                │
               │◄──────────────────────────────── SHIP_FAILED    │
               │  status=COMPENSATING            │               │
               │                │               │                │
               │────────────────── REFUND_PAY ──►               │
               │◄────────────────── PAY_REFUNDED │               │
               │                │               │                │
               │ RELEASE_INV ──►│               │               │
               │◄── INV_RELEASED│               │               │
               │  status=FAILED │               │               │
```

---

## 6. Compensating Transaction Mantığı

### Nedensellik Zinciri

Her başarısızlık, belirli compensating işlemler tetikler. Sıra **her zaman tersten** uygulanır:

```
Başarılı Adımlar (İleri)           Compensating Adımlar (Geri)
─────────────────────────────      ─────────────────────────────
1. Reserve Inventory          →    Release Inventory
2. Process Payment            →    Refund Payment
3. Create Shipment            →    Cancel Shipment
4. Send Notification          →    (geri alınamaz, idempotent)
```

### Compensating Transaction'ın Başarısız Olması?

Bu gerçek bir sorundur. Çözüm yaklaşımları:

1. **Retry mekanizması** — RabbitMQ listener retry ile tekrar denenir (bu projede `max-attempts: 5`)
2. **Dead Letter Queue** — 5 denemede başarısız olan mesajlar DLQ'ya gider, alarm tetiklenir
3. **Outbox Pattern** — DB'ye yaz, sonra publish et (at-least-once delivery garantisi)
4. **Saga State Machine** — Her adım kalıcı olarak kaydedilir, kaldığı yerden devam edilir

---

### Idempotency (Tekrarlanabilirlik)

Her servis **aynı komutu birden fazla kez alırsa ne olur?** Güvenli olmalı:

```java
// İdeal: orderId ile rezervasyon zaten varsa tekrar işlem yapma
if (reservations.containsKey(cmd.getOrderId())) {
    // Zaten yapıldı, aynı sonucu döndür
    publishEvent(INVENTORY_RESERVED, existingReservation);
    return;
}
```

Bu projede basitlik adına tam idempotency uygulanmamıştır. Production'da her servis idempotent olmalıdır.

---

## 7. Teknik Kararlar ve Gerekçeleri

### Spring Kafka: Her Olay Tipi İçin Ayrı `@KafkaListener`

RabbitMQ'daki `@RabbitListener(sınıf) + @RabbitHandler(metod)` yapısının aksine, Spring Kafka'da her olay tipi için bağımsız bir `@KafkaListener` metodu tanımlanır:

```java
// Orkestratörde her olay tipi → kendi topic'i → kendi metodu
@KafkaListener(topics = KafkaTopics.EVT_INVENTORY_RESERVED, groupId = "order-saga-group")
public void onInventoryReserved(InventoryReservedEvent event) { ... }

@KafkaListener(topics = KafkaTopics.EVT_PAYMENT_FAILED, groupId = "order-saga-group")
public void onPaymentFailed(PaymentFailedEvent event) { ... }
```

**Nasıl çalışır?**

Producer `JsonSerializer` ile mesaj gönderirken otomatik olarak `__TypeId__` header'ı ekler:
```
__TypeId__: com.saga.shared.events.InventoryReservedEvent
```
Consumer `JsonDeserializer` bu header'ı okur ve metod parametresine uygun Java tipini oluşturur. `switch-case` veya `instanceof` gerektirmez.

---

### Neden Her Komut/Olay İçin Ayrı Topic?

```
❌ Alternatif: Tek topic, karışık mesajlar
   saga-commands ← [reserve, release, process, refund, create, cancel, ...]
   → Tip ayrımı için header veya wrapper DTO gerekir
   → Consumer tüm mesajları alır, ilgisizlerini filtreler

✅ Bu projede: Komut/olay başına ayrı topic
   inventory.reserve  ← sadece ReserveInventoryCommand
   payment.failed     ← sadece PaymentFailedEvent
   → @KafkaListener parametresi direkt doğru tipe bind edilir
   → Consumer group sadece ilgili topic'i tüketir
   → Kafka UI'da her adım bağımsız izlenebilir
```

---

### Neden Message Key = orderId?

```
orderId-A → partition 0  ┐
orderId-B → partition 1  ├─ Her sipariş kendi partition'ına hash'lenir
orderId-A → partition 0  ┘  (aynı key → aynı partition)

Partition 0 sırası:  [RESERVE → RELEASE]  ✅ sıra korunur
                      ↑ ödeme başarısız oldu, compensation adımı
```

Aynı siparişin compensating mesajı, orijinal mesajından **sonra** aynı partition'da işlenir. Bu sayede `RELEASE_INVENTORY` asla `RESERVE_INVENTORY`'den önce gelmez.

---

### Shared Modül Neden Gerekli?

```
Order Service → ReserveInventoryCommand gönderir
                     JsonSerializer: __TypeId__ = com.saga.shared.commands.ReserveInventoryCommand

Inventory Service → inventory.reserve topic'ini dinler
                     JsonDeserializer: __TypeId__ okur → BULDU ✅
```

Her iki servis de `shared` modülüne bağımlı olduğu için:
- Aynı tam nitelikli sınıf adları → `__TypeId__` eşleşir
- Tek kaynak → DTO değişikliği tek yerden yapılır
- Derleme zamanı tip güvenliği

---

### In-Memory Store Kullanımı

Bu projede `ConcurrentHashMap` kullanılmıştır. Gerçek uygulamada:

```java
// Bu proje:          ConcurrentHashMap<String, Order>
// Production için:   Spring Data JPA + PostgreSQL
//                    Spring Data MongoDB
//                    Spring Data Redis (cache)
```

Saga state'ini DB'ye yazmak kritiktir. Uygulama çöküp yeniden başlarsa kaldığı yerden devam edebilmesi gerekir.

---

## 8. Proje Yapısı

```
saga-pattern/
├── pom.xml                          ← Maven Parent (Spring Boot 3.2, Java 21)
├── docker-compose.yml
│
├── shared/                          ← Ortak kütüphane (com.saga:shared)
│   └── src/main/java/com/saga/shared/
│       ├── constants/
│       │   └── KafkaTopics.java          ← tüm topic isimlerinin merkezi tanımı
│       ├── dto/
│       │   ├── OrderItem.java
│       │   └── OrderStatus.java
│       ├── commands/                     ← 7 komut sınıfı (@Data, Lombok)
│       │   ├── ReserveInventoryCommand.java
│       │   ├── ReleaseInventoryCommand.java
│       │   ├── ProcessPaymentCommand.java
│       │   ├── RefundPaymentCommand.java
│       │   ├── CreateShipmentCommand.java
│       │   ├── CancelShipmentCommand.java
│       │   └── SendNotificationCommand.java
│       └── events/                       ← 10 olay sınıfı
│           ├── InventoryReservedEvent.java
│           ├── InventoryReservationFailedEvent.java
│           ├── InventoryReleasedEvent.java
│           ├── PaymentProcessedEvent.java
│           ├── PaymentFailedEvent.java
│           ├── PaymentRefundedEvent.java
│           ├── ShipmentCreatedEvent.java
│           ├── ShipmentFailedEvent.java
│           ├── ShipmentCancelledEvent.java
│           └── NotificationSentEvent.java
│
├── order-service/                    ← Saga Orkestratör + REST API
│   └── src/main/java/com/saga/order/
│       ├── OrderServiceApplication.java
│       ├── config/RabbitMQConfig.java    ← order.events.queue + 10 binding
│       ├── controller/OrderController.java
│       ├── dto/CreateOrderRequest.java
│       ├── model/Order.java
│       ├── repository/OrderRepository.java
│       └── saga/OrderSagaOrchestrator.java  ← ★ SAGA KALBI
│
├── inventory-service/                ← Stok yönetimi
│   └── service/InventoryCommandHandler.java
│       ├── @RabbitListener QUEUE_INVENTORY_RESERVE → handleReserveInventory()
│       └── @RabbitListener QUEUE_INVENTORY_RELEASE → handleReleaseInventory()
│
├── payment-service/                  ← Ödeme (%20 başarısız)
│   └── service/PaymentCommandHandler.java
│       ├── @RabbitListener QUEUE_PAYMENT_PROCESS → handleProcessPayment()
│       └── @RabbitListener QUEUE_PAYMENT_REFUND  → handleRefundPayment()
│
├── shipping-service/                 ← Kargo oluşturma
│   └── service/ShippingCommandHandler.java
│       ├── @RabbitListener QUEUE_SHIPPING_CREATE → handleCreateShipment()
│       └── @RabbitListener QUEUE_SHIPPING_CANCEL → handleCancelShipment()
│
└── notification-service/             ← E-posta bildirimleri
    └── service/NotificationCommandHandler.java
        └── @RabbitListener QUEUE_NOTIFICATION_SEND → handleSendNotification()
```

### RabbitMQ Exchange ve Kuyruk Haritası

```
saga.commands (Direct Exchange)
├── inventory.reserve  ──►  inventory.reserve.queue
├── inventory.release  ──►  inventory.release.queue
├── payment.process    ──►  payment.process.queue
├── payment.refund     ──►  payment.refund.queue
├── shipping.create    ──►  shipping.create.queue
├── shipping.cancel    ──►  shipping.cancel.queue
└── notification.send  ──►  notification.send.queue

saga.events (Direct Exchange)
├── inventory.reserved            ─┐
├── inventory.released            ─┤
├── inventory.reservation.failed  ─┤
├── payment.processed             ─┤── order.events.queue
├── payment.failed                ─┤
├── payment.refunded              ─┤
├── shipping.created              ─┤
├── shipping.cancelled            ─┤
├── shipping.failed               ─┤
└── notification.sent             ─┘
```

---

## 9. Çalıştırma

### Gereksinimler

- Docker ve Docker Compose

### Başlatma

```bash
docker-compose up --build
```

İlk build yaklaşık 3-5 dakika sürer (Maven bağımlılıkları indirilir).

### Servis URL'leri

| Servis | URL | Açıklama |
|---|---|---|
| Order Service | http://localhost:8081 | REST API, saga başlatır |
| Inventory Service | http://localhost:8082 | Stok yönetimi |
| Payment Service | http://localhost:8083 | Ödeme işleme |
| Shipping Service | http://localhost:8084 | Kargo yönetimi |
| Notification Service | http://localhost:8085 | Bildirimler |
| RabbitMQ Yönetim | http://localhost:15672 | admin / admin |

### Durdurma

```bash
docker-compose down -v   # -v ile kuyruklar da temizlenir
```

---

## 10. Test Senaryoları

### Senaryo 1: Başarılı Sipariş

Stokta olan bir ürün (P001-P004) ile sipariş ver:

```bash
curl -X POST http://localhost:8081/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "C001",
    "customerEmail": "musteri@ornek.com",
    "items": [
      {"productId": "P001", "productName": "Laptop", "quantity": 1, "price": 15000},
      {"productId": "P002", "productName": "Klavye", "quantity": 2, "price": 500}
    ]
  }'
```

**Beklenen:** `status: PENDING` → logları izle → `COMPLETED`

---

### Senaryo 2: Stok Yetersiz (Anlık Compensation)

Stoku olmayan ürün (P005 - Kulaklık, stok: 0):

```bash
curl -X POST http://localhost:8081/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "C002",
    "customerEmail": "musteri@ornek.com",
    "items": [
      {"productId": "P005", "productName": "Kulaklık", "quantity": 1, "price": 1200}
    ]
  }'
```

**Beklenen:** `PENDING` → Inventory'den `INVENTORY_RESERVATION_FAILED` → `FAILED`  
Compensation gerekmez çünkü henüz para alınmadı.

---

### Senaryo 3: Ödeme Başarısız (Compensation Zinciri)

Payment service %20 ihtimalle başarısız olur. Birkaç sipariş ver:

```bash
for i in {1..5}; do
  curl -s -X POST http://localhost:8081/orders \
    -H "Content-Type: application/json" \
    -d '{
      "customerId": "C003",
      "customerEmail": "test@ornek.com",
      "items": [{"productId": "P003", "quantity": 1, "price": 500, "productName": "Mouse"}]
    }' | python3 -m json.tool
  echo "---"
done
```

**Beklenen:** Bazı siparişler `COMPLETED`, bazıları `FAILED`.  
`FAILED` olanlar için log'da şunu görürsün:
```
[Saga] ⚠️  Ödeme başarısız → Compensation: stok serbest bırakılıyor
[Saga] ❌ Stok serbest bırakıldı — Saga FAILED
```

---

### Sipariş Durumunu Sorgula

```bash
# Tek sipariş
curl http://localhost:8081/orders/{orderId}

# Tüm siparişler
curl http://localhost:8081/orders
```

**Olası durumlar:**

| Status | Açıklama |
|---|---|
| `PENDING` | Saga başladı, stok rezervasyonu bekleniyor |
| `INVENTORY_RESERVED` | Stok rezerve edildi, ödeme bekleniyor |
| `PAYMENT_PROCESSED` | Ödeme alındı, kargo bekleniyor |
| `SHIPMENT_CREATED` | Kargo oluşturuldu, bildirim bekleniyor |
| `COMPLETED` | Tüm adımlar başarılı ✅ |
| `COMPENSATING` | Hata oluştu, geri alınıyor ⚠️ |
| `FAILED` | Compensation tamamlandı, sipariş iptal ❌ |

---

### RabbitMQ Yönetim Paneli

`http://localhost:15672` adresinden RabbitMQ'ya bağlan (admin/admin).

**İzlenecekler:**
- **Exchanges** sekmesi → `saga.commands` ve `saga.events` exchange'leri
- **Queues** sekmesi → 8 kuyruk ve mesaj sayıları
- **Connections** → 5 servis bağlantısı

---

## Sıkça Sorulan Sorular

**S: Saga başarısız olursa ve compensating işlem de başarısız olursa ne olur?**

Spring AMQP'nin retry mekanizması (`max-attempts: 5`) devreye girer. 5 denemede başarısız olursa mesaj kuyrukta kalır ya da Dead Letter Queue'ya gider. Production'da bu senaryolar için alert ve manuel müdahale süreci tanımlanmalıdır.

**S: İki sipariş aynı anda aynı ürünü stoktan tükettirse?**

Bu "lost update" sorunudur. Çözüm: Inventory service'de optimistic locking veya veritabanı düzeyinde `SELECT FOR UPDATE`. Bu örnek in-memory olduğu için `ConcurrentHashMap` ile thread safety sağlanmıştır ama distributed ortamda yeterli değildir.

**S: Order service yeniden başlarsa saga'nın durumunu kaybeder mi?**

Bu örnekte evet, çünkü in-memory store kullanıyoruz. Production'da saga state'i veritabanına yazılmalı (`OrderSagaState` tablosu). Axon Framework, Eventuate gibi kütüphaneler bunu otomatik yönetir.

**S: Saga Pattern yerine ne zaman başka bir şey kullanmalıyım?**

Servisler arasında güçlü tutarlılık şarttır, gecikme toleransı yoktur ve az sayıda servis varsa 2PC veya monolitik mimari değerlendirilebilir. Saga, nihai tutarlılığı kabul eden sistemler için uygundur.

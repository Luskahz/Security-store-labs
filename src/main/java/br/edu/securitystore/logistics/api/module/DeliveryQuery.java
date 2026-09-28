package br.edu.securitystore.logistics.api.module;

import java.time.Instant;
import java.util.Optional;

public interface DeliveryQuery {
    Optional<DeliverySummary> findByOrderId(Long orderId);

    enum DeliveryStatus { PREPARING, SHIPPED, IN_TRANSIT, OUT_FOR_DELIVERY, DELIVERED, CANCELLED }

    record DeliverySummary(Long id, Long orderId, DeliveryStatus status, String trackingCode,
            Instant estimatedDeliveryAt, Instant shippedAt, Instant deliveredAt) {}
}

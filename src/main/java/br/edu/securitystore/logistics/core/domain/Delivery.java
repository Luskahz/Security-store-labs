package br.edu.securitystore.logistics.core.domain;

import br.edu.securitystore.logistics.api.module.DeliveryQuery.DeliveryStatus;
import java.time.Instant;

public class Delivery {
    private final Long id;
    private final Long orderId;
    private final String trackingCode;
    private final Instant estimatedDeliveryAt;
    private DeliveryStatus status;
    private Instant shippedAt;
    private Instant deliveredAt;

    public Delivery(Long orderId, String trackingCode, Instant estimatedDeliveryAt) {
        this(null, orderId, DeliveryStatus.PREPARING, trackingCode, estimatedDeliveryAt, null, null);
    }

    public Delivery(Long id, Long orderId, DeliveryStatus status, String trackingCode,
            Instant estimatedDeliveryAt, Instant shippedAt, Instant deliveredAt) {
        this.id=id; this.orderId=orderId; this.status=status; this.trackingCode=trackingCode;
        this.estimatedDeliveryAt=estimatedDeliveryAt; this.shippedAt=shippedAt; this.deliveredAt=deliveredAt;
    }

    public void updateStatus(DeliveryStatus next) {
        if (next == null) throw new IllegalArgumentException("Status de entrega obrigatório");
        if (status == next) return;
        boolean valid = switch (status) {
            case PREPARING -> next == DeliveryStatus.SHIPPED || next == DeliveryStatus.CANCELLED;
            case SHIPPED -> next == DeliveryStatus.IN_TRANSIT || next == DeliveryStatus.OUT_FOR_DELIVERY || next == DeliveryStatus.DELIVERED;
            case IN_TRANSIT -> next == DeliveryStatus.OUT_FOR_DELIVERY || next == DeliveryStatus.DELIVERED;
            case OUT_FOR_DELIVERY -> next == DeliveryStatus.IN_TRANSIT || next == DeliveryStatus.DELIVERED;
            case DELIVERED, CANCELLED -> false;
        };
        if (!valid) throw new IllegalStateException("Transição de entrega inválida: " + status + " -> " + next);
        status = next;
        if (next == DeliveryStatus.SHIPPED) shippedAt = Instant.now();
        if (next == DeliveryStatus.DELIVERED) deliveredAt = Instant.now();
    }

    public Long getId(){return id;} public Long getOrderId(){return orderId;}
    public DeliveryStatus getStatus(){return status;} public String getTrackingCode(){return trackingCode;}
    public Instant getEstimatedDeliveryAt(){return estimatedDeliveryAt;} public Instant getShippedAt(){return shippedAt;}
    public Instant getDeliveredAt(){return deliveredAt;}
}

package br.edu.securitystore.logistics.infra.persistence;

import br.edu.securitystore.logistics.api.module.DeliveryQuery.DeliveryStatus;
import br.edu.securitystore.logistics.core.domain.Delivery;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name="deliveries", uniqueConstraints=@UniqueConstraint(columnNames="orderId"))
class DeliveryEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
    @Column(nullable=false,unique=true) Long orderId;
    @Enumerated(EnumType.STRING) @Column(nullable=false) DeliveryStatus status;
    @Column(nullable=false) String trackingCode;
    @Column(nullable=false) Instant estimatedDeliveryAt;
    Instant shippedAt;
    Instant deliveredAt;
    protected DeliveryEntity(){}
    DeliveryEntity(Delivery delivery){id=delivery.getId();orderId=delivery.getOrderId();status=delivery.getStatus();trackingCode=delivery.getTrackingCode();
        estimatedDeliveryAt=delivery.getEstimatedDeliveryAt();shippedAt=delivery.getShippedAt();deliveredAt=delivery.getDeliveredAt();}
    Delivery toDomain(){return new Delivery(id,orderId,status,trackingCode,estimatedDeliveryAt,shippedAt,deliveredAt);}
}

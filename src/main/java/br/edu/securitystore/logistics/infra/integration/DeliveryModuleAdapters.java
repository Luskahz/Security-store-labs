package br.edu.securitystore.logistics.infra.integration;

import br.edu.securitystore.logistics.api.module.DeliveryOperations;
import br.edu.securitystore.logistics.api.module.DeliveryQuery;
import br.edu.securitystore.logistics.core.application.DeliveryService;
import br.edu.securitystore.logistics.core.domain.Delivery;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
class DeliveryOperationsAdapter implements DeliveryOperations {
    private final DeliveryService service;
    DeliveryOperationsAdapter(DeliveryService service){this.service=service;}
    public DeliveryQuery.DeliverySummary createForOrder(Long orderId){return DeliverySummaryMapper.from(service.createForOrder(orderId));}
    public DeliveryQuery.DeliverySummary updateStatus(Long orderId,DeliveryQuery.DeliveryStatus status){return DeliverySummaryMapper.from(service.updateStatus(orderId,status));}
}

@Component
class DeliveryQueryAdapter implements DeliveryQuery {
    private final br.edu.securitystore.logistics.core.repository.DeliveryRepository repository;
    DeliveryQueryAdapter(br.edu.securitystore.logistics.core.repository.DeliveryRepository repository){this.repository=repository;}
    public Optional<DeliveryQuery.DeliverySummary> findByOrderId(Long orderId){
        return repository.findByOrderId(orderId).map(DeliverySummaryMapper::from);
    }
}

final class DeliverySummaryMapper {
    private DeliverySummaryMapper(){}
    static DeliveryQuery.DeliverySummary from(Delivery delivery){
        return new DeliveryQuery.DeliverySummary(delivery.getId(),delivery.getOrderId(),delivery.getStatus(),delivery.getTrackingCode(),
                delivery.getEstimatedDeliveryAt(),delivery.getShippedAt(),delivery.getDeliveredAt());
    }
}

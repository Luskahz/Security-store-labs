package br.edu.securitystore.logistics.api.module;

public interface DeliveryOperations {
    DeliveryQuery.DeliverySummary createForOrder(Long orderId);
    DeliveryQuery.DeliverySummary updateStatus(Long orderId, DeliveryQuery.DeliveryStatus status);
}

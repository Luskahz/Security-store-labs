package br.edu.securitystore.logistics.api.http.controller;

import br.edu.securitystore.logistics.api.module.DeliveryQuery.DeliveryStatus;
import br.edu.securitystore.logistics.api.module.DeliveryQuery.DeliverySummary;
import br.edu.securitystore.logistics.core.application.DeliveryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/deliveries")
public class DeliveryController {
    private final DeliveryService service;
    public DeliveryController(DeliveryService service){this.service=service;}

    public record UpdateDeliveryRequest(@NotNull DeliveryStatus status){}

    @GetMapping("/orders/{orderId}")
    @PreAuthorize("hasAuthority('LOGISTICS_DELIVERY_READ') or hasAuthority('SALES_ORDER_READ_ALL')")
    public DeliverySummary byOrder(@PathVariable Long orderId){return DeliverySummaryMapper.from(service.getByOrderId(orderId));}

    @PatchMapping("/orders/{orderId}")
    @PreAuthorize("hasAuthority('LOGISTICS_DELIVERY_UPDATE')")
    public DeliverySummary update(@PathVariable Long orderId,@Valid @RequestBody UpdateDeliveryRequest request){
        return DeliverySummaryMapper.from(service.updateStatus(orderId,request.status()));
    }
}

final class DeliverySummaryMapper {
    private DeliverySummaryMapper(){}
    static DeliverySummary from(br.edu.securitystore.logistics.core.domain.Delivery delivery){
        return new DeliverySummary(delivery.getId(),delivery.getOrderId(),delivery.getStatus(),delivery.getTrackingCode(),
                delivery.getEstimatedDeliveryAt(),delivery.getShippedAt(),delivery.getDeliveredAt());
    }
}

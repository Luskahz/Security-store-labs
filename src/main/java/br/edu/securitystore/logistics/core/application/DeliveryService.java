package br.edu.securitystore.logistics.core.application;

import br.edu.securitystore.logistics.api.module.DeliveryQuery.DeliveryStatus;
import br.edu.securitystore.logistics.core.domain.Delivery;
import br.edu.securitystore.logistics.core.repository.DeliveryRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeliveryService {
    private final DeliveryRepository deliveries;
    public DeliveryService(DeliveryRepository deliveries){this.deliveries=deliveries;}

    @Transactional
    public Delivery createForOrder(Long orderId) {
        return deliveries.findByOrderId(orderId).orElseGet(() -> deliveries.save(
                new Delivery(orderId, trackingCode(orderId), Instant.now().plus(5, ChronoUnit.DAYS))));
    }

    @Transactional(readOnly=true)
    public Delivery getByOrderId(Long orderId) {
        return deliveries.findByOrderId(orderId).orElseThrow(() -> new NoSuchElementException("Entrega não encontrada"));
    }

    @Transactional
    public Delivery updateStatus(Long orderId, DeliveryStatus status) {
        Delivery delivery=getByOrderId(orderId); delivery.updateStatus(status); return deliveries.save(delivery);
    }

    private String trackingCode(Long orderId) { return "LAB-%08d".formatted(orderId); }
}

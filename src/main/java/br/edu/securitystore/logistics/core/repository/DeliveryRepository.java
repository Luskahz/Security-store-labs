package br.edu.securitystore.logistics.core.repository;

import br.edu.securitystore.logistics.core.domain.Delivery;
import java.util.Optional;

public interface DeliveryRepository {
    Optional<Delivery> findByOrderId(Long orderId);
    Delivery save(Delivery delivery);
}

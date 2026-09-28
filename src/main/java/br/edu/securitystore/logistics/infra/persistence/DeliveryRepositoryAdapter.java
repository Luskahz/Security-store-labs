package br.edu.securitystore.logistics.infra.persistence;

import br.edu.securitystore.logistics.core.domain.Delivery;
import br.edu.securitystore.logistics.core.repository.DeliveryRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
class DeliveryRepositoryAdapter implements DeliveryRepository {
    private final JpaDeliveryRepository jpa;
    DeliveryRepositoryAdapter(JpaDeliveryRepository jpa){this.jpa=jpa;}
    public Optional<Delivery> findByOrderId(Long orderId){return jpa.findByOrderId(orderId).map(DeliveryEntity::toDomain);}
    public Delivery save(Delivery delivery){return jpa.save(new DeliveryEntity(delivery)).toDomain();}
}

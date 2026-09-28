package br.edu.securitystore.logistics.infra.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

interface JpaDeliveryRepository extends JpaRepository<DeliveryEntity,Long>{
    Optional<DeliveryEntity> findByOrderId(Long orderId);
}

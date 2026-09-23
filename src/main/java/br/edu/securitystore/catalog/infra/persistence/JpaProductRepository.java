package br.edu.securitystore.catalog.infra.persistence;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

interface JpaProductRepository extends JpaRepository<ProductEntity,Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE) Optional<ProductEntity> findLockedById(Long id);
}

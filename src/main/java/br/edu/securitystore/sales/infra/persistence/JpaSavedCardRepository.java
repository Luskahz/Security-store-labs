package br.edu.securitystore.sales.infra.persistence;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

interface JpaSavedCardRepository extends JpaRepository<SavedCardEntity,Long> {
    List<SavedCardEntity> findByIdentityIdOrderByCreatedAtDesc(Long identityId);
    Optional<SavedCardEntity> findByIdentityIdAndToken(Long identityId, String token);
    void deleteByIdentityIdAndToken(Long identityId, String token);
}

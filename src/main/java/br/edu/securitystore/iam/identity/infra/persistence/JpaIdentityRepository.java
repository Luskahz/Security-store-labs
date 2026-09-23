package br.edu.securitystore.iam.identity.infra.persistence;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
interface JpaIdentityRepository extends JpaRepository<IdentityEntity,Long> { Optional<IdentityEntity> findByEmailIgnoreCase(String email); }

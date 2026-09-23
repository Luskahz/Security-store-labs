package br.edu.securitystore.iam.identity.infra.persistence;

import br.edu.securitystore.iam.identity.core.domain.Identity;
import br.edu.securitystore.iam.identity.core.repository.IdentityRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
class IdentityRepositoryAdapter implements IdentityRepository {
    private final JpaIdentityRepository jpa;
    IdentityRepositoryAdapter(JpaIdentityRepository jpa){this.jpa=jpa;}
    public Optional<Identity> byId(Long id){return jpa.findById(id).map(IdentityEntity::toDomain);}
    public Optional<Identity> byEmail(String email){return jpa.findByEmailIgnoreCase(email).map(IdentityEntity::toDomain);}
    public List<Identity> all(){return jpa.findAll().stream().map(IdentityEntity::toDomain).toList();}
    public Identity save(Identity value){return jpa.save(new IdentityEntity(value)).toDomain();}
}

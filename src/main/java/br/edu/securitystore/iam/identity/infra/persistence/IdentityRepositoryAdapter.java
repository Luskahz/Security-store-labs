package br.edu.securitystore.iam.identity.infra.persistence;

import br.edu.securitystore.iam.identity.core.domain.Identity;
import br.edu.securitystore.iam.identity.core.repository.IdentityRepository;
import br.edu.securitystore.platform.privacy.PiiProtection;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
class IdentityRepositoryAdapter implements IdentityRepository {
    private final JpaIdentityRepository jpa; private final PiiProtection pii;
    IdentityRepositoryAdapter(JpaIdentityRepository jpa,PiiProtection pii){this.jpa=jpa;this.pii=pii;}
    public Optional<Identity> byId(Long id){return jpa.findById(id).map(this::domain);}
    public Optional<Identity> byEmail(String email){return jpa.findByEmailIgnoreCase(email).map(this::domain);}
    public Optional<Identity> byCpf(String cpf){return jpa.findByCpfFingerprint(pii.fingerprint(cpf)).map(this::domain);}
    public List<Identity> all(){return jpa.findAll().stream().map(this::domain).toList();}
    public Identity save(Identity value){IdentityEntity entity=new IdentityEntity(value);if(value.cpf()!=null){entity.cpfEncrypted=pii.encrypt(value.cpf());entity.cpfFingerprint=pii.fingerprint(value.cpf());}entity.phoneEncrypted=pii.encrypt(value.phone());entity.streetEncrypted=pii.encrypt(value.street());entity.numberEncrypted=pii.encrypt(value.number());entity.complementEncrypted=pii.encrypt(value.complement());entity.neighborhoodEncrypted=pii.encrypt(value.neighborhood());entity.cityEncrypted=pii.encrypt(value.city());entity.stateEncrypted=pii.encrypt(value.state());entity.postalCodeEncrypted=pii.encrypt(value.postalCode());return domain(jpa.save(entity));}
    private Identity domain(IdentityEntity entity){return entity.toDomain(pii.decrypt(entity.cpfEncrypted),pii.decrypt(entity.phoneEncrypted),pii.decrypt(entity.streetEncrypted),pii.decrypt(entity.numberEncrypted),pii.decrypt(entity.complementEncrypted),pii.decrypt(entity.neighborhoodEncrypted),pii.decrypt(entity.cityEncrypted),pii.decrypt(entity.stateEncrypted),pii.decrypt(entity.postalCodeEncrypted));}
}

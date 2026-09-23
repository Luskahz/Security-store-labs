package br.edu.securitystore.iam.identity.core.repository;

import br.edu.securitystore.iam.identity.core.domain.Identity;
import java.util.List;
import java.util.Optional;

public interface IdentityRepository {
    Optional<Identity> byId(Long id);
    Optional<Identity> byEmail(String email);
    List<Identity> all();
    Identity save(Identity identity);
}

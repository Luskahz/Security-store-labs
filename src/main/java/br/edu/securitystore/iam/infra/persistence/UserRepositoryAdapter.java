package br.edu.securitystore.iam.infra.persistence;

import br.edu.securitystore.iam.core.domain.UserAccount;
import br.edu.securitystore.iam.core.repository.UserRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryAdapter implements UserRepository {
    private final JpaUserRepository jpa;
    public UserRepositoryAdapter(JpaUserRepository jpa) { this.jpa=jpa; }
    public Optional<UserAccount> findByEmailIgnoreCase(String email) { return jpa.findByEmailIgnoreCase(email).map(this::map); }
    public boolean existsByEmailIgnoreCase(String email) { return jpa.existsByEmailIgnoreCase(email); }
    public UserAccount save(UserAccount user) { return map(jpa.save(new UserEntity(user.id(),user.name(),user.email(),user.passwordHash(),user.role()))); }
    private UserAccount map(UserEntity entity) { return new UserAccount(entity.id,entity.name,entity.email,entity.passwordHash,entity.role); }
}

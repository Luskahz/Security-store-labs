package br.edu.securitystore.iam.core.repository;

import br.edu.securitystore.iam.core.domain.UserAccount;
import java.util.Optional;

public interface UserRepository {
    Optional<UserAccount> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
    UserAccount save(UserAccount user);
}

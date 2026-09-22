package br.edu.securitystore.iam.core.application;

import br.edu.securitystore.iam.core.domain.UserAccount;
import br.edu.securitystore.iam.core.repository.UserRepository;
import br.edu.securitystore.iam.core.port.PasswordHasher;
import org.springframework.stereotype.Service;

@Service
public class IamService {
    private final UserRepository users;
    private final PasswordHasher hasher;
    public IamService(UserRepository users, PasswordHasher hasher) { this.users = users; this.hasher = hasher; }
    public UserAccount register(String name, String email, String password) {
        String normalized = email.trim().toLowerCase();
        if (users.existsByEmailIgnoreCase(normalized)) throw new DuplicateEmailException();
        return users.save(new UserAccount(null, name.trim(), normalized, hasher.hash(password), "CUSTOMER"));
    }
    public UserAccount findByEmail(String email) { return users.findByEmailIgnoreCase(email).orElseThrow(); }
    public static class DuplicateEmailException extends RuntimeException {}
}

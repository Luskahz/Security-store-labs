package br.edu.securitystore.iam.core.application;

import br.edu.securitystore.iam.UserAccount;
import br.edu.securitystore.iam.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class IamService {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    public IamService(UserRepository users, PasswordEncoder encoder) { this.users = users; this.encoder = encoder; }
    public UserAccount register(String name, String email, String password) {
        String normalized = email.trim().toLowerCase();
        if (users.existsByEmailIgnoreCase(normalized)) throw new DuplicateEmailException();
        return users.save(new UserAccount(name.trim(), normalized, encoder.encode(password), "CUSTOMER"));
    }
    public UserAccount findByEmail(String email) { return users.findByEmailIgnoreCase(email).orElseThrow(); }
    public static class DuplicateEmailException extends RuntimeException {}
}

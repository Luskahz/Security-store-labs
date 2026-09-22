package br.edu.securitystore.iam.infra.integration;

import br.edu.securitystore.iam.core.port.PasswordHasher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BcryptPasswordHasher implements PasswordHasher {
    private final PasswordEncoder encoder;
    public BcryptPasswordHasher(PasswordEncoder encoder) { this.encoder=encoder; }
    public String hash(String password) { return encoder.encode(password); }
}

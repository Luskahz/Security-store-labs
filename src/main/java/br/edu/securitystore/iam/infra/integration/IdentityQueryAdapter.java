package br.edu.securitystore.iam.infra.integration;

import br.edu.securitystore.iam.api.module.IdentityQuery;

import br.edu.securitystore.iam.core.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class IdentityQueryAdapter implements IdentityQuery {
    private final UserRepository users;
    public IdentityQueryAdapter(UserRepository users) { this.users = users; }
    public java.util.Optional<Identity> findByEmail(String email) {
        return users.findByEmailIgnoreCase(email).map(u -> new Identity(u.id(), u.name(), u.email(), u.role()));
    }
    public java.util.Optional<AuthenticationAccount> authenticationAccount(String email) {
        return users.findByEmailIgnoreCase(email).map(u -> new AuthenticationAccount(u.email(), u.passwordHash(), u.role()));
    }
}

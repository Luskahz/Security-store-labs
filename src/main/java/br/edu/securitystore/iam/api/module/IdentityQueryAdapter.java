package br.edu.securitystore.iam.api.module;

import br.edu.securitystore.iam.UserRepository;
import org.springframework.stereotype.Component;

@Component
class IdentityQueryAdapter implements IdentityQuery {
    private final UserRepository users;
    IdentityQueryAdapter(UserRepository users) { this.users = users; }
    public java.util.Optional<Identity> findByEmail(String email) {
        return users.findByEmailIgnoreCase(email).map(u -> new Identity(u.getId(), u.getName(), u.getEmail(), u.getRole()));
    }
    public java.util.Optional<AuthenticationAccount> authenticationAccount(String email) {
        return users.findByEmailIgnoreCase(email).map(u -> new AuthenticationAccount(u.getEmail(), u.getPasswordHash(), u.getRole()));
    }
}

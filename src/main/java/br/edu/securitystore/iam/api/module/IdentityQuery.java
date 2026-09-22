package br.edu.securitystore.iam.api.module;

import java.util.Optional;

/** Contrato interno publicado pelo contexto de identidade. */
public interface IdentityQuery {
    record Identity(Long id, String name, String email, String role) {}
    record AuthenticationAccount(String email, String passwordHash, String role) {}
    Optional<Identity> findByEmail(String email);
    Optional<AuthenticationAccount> authenticationAccount(String email);
}

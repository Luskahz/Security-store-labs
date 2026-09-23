package br.edu.securitystore.iam.identity.api.module;

import java.util.Optional;

public interface IdentityQuery {
    record IdentityView(Long id, String name, String email, boolean active) {}
    Optional<IdentityView> byId(Long id);
    Optional<IdentityView> byEmail(String email);
}

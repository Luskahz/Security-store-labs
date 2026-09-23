package br.edu.securitystore.iam.authentication.api.module;

public interface AuthenticationSessionAdministration {
    void revokeAll(Long identityId);
    boolean active(Long identityId, String sessionId);
}

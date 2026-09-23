package br.edu.securitystore.iam.authorization.api.module;

public interface AuthorizationProvisioning {
    void assignUser(Long identityId);
    void assignAdmin(Long identityId);
}

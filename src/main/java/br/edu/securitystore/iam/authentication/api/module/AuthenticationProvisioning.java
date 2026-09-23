package br.edu.securitystore.iam.authentication.api.module;

public interface AuthenticationProvisioning {
    void createAccount(Long identityId, String password);
}

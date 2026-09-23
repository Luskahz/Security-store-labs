package br.edu.securitystore.iam.authentication.infra.integration;

import br.edu.securitystore.iam.authentication.api.module.AuthenticationProvisioning;
import br.edu.securitystore.iam.authentication.api.module.AuthenticationSessionAdministration;
import br.edu.securitystore.iam.authentication.core.application.AuthenticationService;
import org.springframework.stereotype.Component;

final class AuthenticationModuleAdapters { private AuthenticationModuleAdapters() {} }

@Component
class AuthenticationProvisioningAdapter implements AuthenticationProvisioning {
    private final AuthenticationService service;
    AuthenticationProvisioningAdapter(AuthenticationService service) { this.service = service; }
    public void createAccount(Long identityId, String password) { service.createAccount(identityId, password); }
}

@Component
class AuthenticationSessionAdministrationAdapter implements AuthenticationSessionAdministration {
    private final AuthenticationService service;
    AuthenticationSessionAdministrationAdapter(AuthenticationService service) { this.service = service; }
    public void revokeAll(Long identityId) { service.revokeAll(identityId); }
    public boolean active(Long identityId, String sessionId) { return service.active(identityId, sessionId); }
}

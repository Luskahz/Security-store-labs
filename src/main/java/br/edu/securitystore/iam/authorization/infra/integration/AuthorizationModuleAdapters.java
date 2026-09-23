package br.edu.securitystore.iam.authorization.infra.integration;

import br.edu.securitystore.iam.authorization.api.module.AuthorizationQuery;
import br.edu.securitystore.iam.authorization.api.module.AuthorizationProvisioning;
import br.edu.securitystore.iam.authorization.core.application.AuthorizationService;
import java.util.Set;
import org.springframework.stereotype.Component;

final class AuthorizationModuleAdapters { private AuthorizationModuleAdapters() {} }

@Component
class AuthorizationQueryAdapter implements AuthorizationQuery {
    private final AuthorizationService service;
    AuthorizationQueryAdapter(AuthorizationService service) { this.service = service; }
    public Set<String> authorities(Long id) { return service.authorities(id); }
    public Set<String> roles(Long id) { return service.roleNames(id); }
}

@Component
class AuthorizationProvisioningAdapter implements AuthorizationProvisioning {
    private final AuthorizationService service;
    AuthorizationProvisioningAdapter(AuthorizationService service) { this.service = service; }
    public void assignUser(Long id) { service.assignUserRole(id, service.roles().stream().filter(r -> r.name().equals("USER")).findFirst().orElseThrow().id()); }
    public void assignAdmin(Long id) { service.assignUserRole(id, service.roles().stream().filter(r -> r.name().equals("ADMIN")).findFirst().orElseThrow().id()); }
}

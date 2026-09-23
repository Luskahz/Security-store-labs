package br.edu.securitystore.iam.identity.infra.integration;

import br.edu.securitystore.iam.authentication.api.module.AuthenticationProvisioning;
import br.edu.securitystore.iam.authentication.api.module.AuthenticationSessionAdministration;
import br.edu.securitystore.iam.authorization.api.module.AuthorizationProvisioning;
import br.edu.securitystore.iam.identity.core.port.*;
import org.springframework.stereotype.Component;

final class IdentityProvisioningAdapters { private IdentityProvisioningAdapters(){} }

@Component class AccountProvisionerAdapter implements AccountProvisioner {
 private final AuthenticationProvisioning authentication;AccountProvisionerAdapter(AuthenticationProvisioning authentication){this.authentication=authentication;}
 public void create(Long id,String password){authentication.createAccount(id,password);}
}
@Component class DefaultRoleAssignerAdapter implements DefaultRoleAssigner {
 private final AuthorizationProvisioning authorization;DefaultRoleAssignerAdapter(AuthorizationProvisioning authorization){this.authorization=authorization;}
 public void assignUser(Long id){authorization.assignUser(id);}
}
@Component class SessionRevokerAdapter implements SessionRevoker {
 private final AuthenticationSessionAdministration authentication;SessionRevokerAdapter(AuthenticationSessionAdministration authentication){this.authentication=authentication;}
 public void revokeAll(Long id){authentication.revokeAll(id);}
}

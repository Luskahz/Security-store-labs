package br.edu.securitystore.iam.identity.infra.integration;

import br.edu.securitystore.iam.authentication.api.module.AuthenticationOperations;
import br.edu.securitystore.iam.authorization.api.module.AuthoritiesQuery;
import br.edu.securitystore.iam.identity.core.port.*;
import org.springframework.stereotype.Component;

final class IdentityProvisioningAdapters { private IdentityProvisioningAdapters(){} }

@Component class AccountProvisionerAdapter implements AccountProvisioner {
 private final AuthenticationOperations authentication;AccountProvisionerAdapter(AuthenticationOperations authentication){this.authentication=authentication;}
 public void create(Long id,String password){authentication.createAccount(id,password);}
}
@Component class DefaultRoleAssignerAdapter implements DefaultRoleAssigner {
 private final AuthoritiesQuery authorization;DefaultRoleAssignerAdapter(AuthoritiesQuery authorization){this.authorization=authorization;}
 public void assignUser(Long id){authorization.assignUser(id);}
}
@Component class SessionRevokerAdapter implements SessionRevoker {
 private final AuthenticationOperations authentication;SessionRevokerAdapter(AuthenticationOperations authentication){this.authentication=authentication;}
 public void revokeAll(Long id){authentication.revokeAll(id);}
}

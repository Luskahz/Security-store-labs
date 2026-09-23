package br.edu.securitystore.iam.authentication.infra.integration;
import br.edu.securitystore.iam.authentication.api.module.AuthenticationOperations;
import br.edu.securitystore.iam.authentication.core.application.AuthenticationService;
import br.edu.securitystore.iam.authentication.core.domain.AuthenticationAccount;
import org.springframework.stereotype.Component;
@Component class AuthenticationOperationsAdapter implements AuthenticationOperations {
 private final AuthenticationService service;AuthenticationOperationsAdapter(AuthenticationService service){this.service=service;}
 public void createAccount(Long id,String password){service.createAccount(id,password);}
 public void revokeAll(Long id){service.revokeAll(id);}
 public boolean active(Long id,String sessionId){return service.active(id,sessionId);}
 public boolean accountEnabled(Long id){return service.accountEnabled(id);}
 public java.util.Optional<Credential> credential(Long id){return service.credential(id).map(a->new Credential(a.passwordHash(),a.status()==AuthenticationAccount.Status.ENABLED));}
}

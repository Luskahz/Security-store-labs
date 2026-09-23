package br.edu.securitystore.iam.authorization.infra.integration;
import br.edu.securitystore.iam.authorization.api.module.AuthoritiesQuery;
import br.edu.securitystore.iam.authorization.core.application.AuthorizationService;
import java.util.Set;import org.springframework.stereotype.Component;
@Component class AuthoritiesQueryAdapter implements AuthoritiesQuery {
 private final AuthorizationService service;AuthoritiesQueryAdapter(AuthorizationService service){this.service=service;}
 public Set<String> authorities(Long id){return service.authorities(id);}
 public Set<String> roles(Long id){return service.roleNames(id);}
 public void assignUser(Long id){service.assignUserRole(id,service.roles().stream().filter(r->r.name().equals("USER")).findFirst().orElseThrow().id());}
 public void assignAdmin(Long id){service.assignUserRole(id,service.roles().stream().filter(r->r.name().equals("ADMIN")).findFirst().orElseThrow().id());}
}

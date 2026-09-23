package br.edu.securitystore.iam.authentication.infra.integration;
import br.edu.securitystore.iam.authentication.core.port.IdentityLookup;
import br.edu.securitystore.iam.identity.api.module.IdentityQuery;
import java.util.Optional;import org.springframework.stereotype.Component;
@Component class IdentityLookupAdapter implements IdentityLookup {
 private final IdentityQuery identities;IdentityLookupAdapter(IdentityQuery identities){this.identities=identities;}
 public Optional<Identity> byEmail(String email){return identities.byEmail(email).map(this::map);}
 public Optional<Identity> byId(Long id){return identities.byId(id).map(this::map);}
 private Identity map(IdentityQuery.IdentityView v){return new Identity(v.id(),v.name(),v.email(),v.active());}
}

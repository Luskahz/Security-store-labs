package br.edu.securitystore.iam.identity.infra.integration;

import br.edu.securitystore.iam.identity.api.module.IdentityQuery;
import br.edu.securitystore.iam.identity.core.domain.Identity;
import br.edu.securitystore.iam.identity.core.repository.IdentityRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
class IdentityQueryAdapter implements IdentityQuery {
    private final IdentityRepository identities;
    IdentityQueryAdapter(IdentityRepository identities){this.identities=identities;}
    public Optional<IdentityView> byId(Long id){return identities.byId(id).map(this::view);}
    public Optional<IdentityView> byEmail(String email){return identities.byEmail(email).map(this::view);}
    private IdentityView view(Identity identity){boolean complete=identity.cpf()!=null&&identity.phone()!=null&&identity.street()!=null&&identity.number()!=null&&identity.neighborhood()!=null&&identity.city()!=null&&identity.state()!=null&&identity.postalCode()!=null;return new IdentityView(identity.id(),identity.name(),identity.email(),identity.status()==Identity.Status.ACTIVE,complete,identity.phone(),identity.street(),identity.number(),identity.complement(),identity.neighborhood(),identity.city(),identity.state(),identity.postalCode());}
}

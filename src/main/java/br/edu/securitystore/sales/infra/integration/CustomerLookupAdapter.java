package br.edu.securitystore.sales.infra.integration;
import br.edu.securitystore.iam.identity.api.module.IdentityQuery;
import br.edu.securitystore.iam.authorization.api.module.AuthorizationQuery;
import br.edu.securitystore.sales.core.port.CustomerLookup;
import br.edu.securitystore.sales.core.domain.Order;
import org.springframework.stereotype.Component;
@Component public class CustomerLookupAdapter implements CustomerLookup {
    private final IdentityQuery identities;
    private final AuthorizationQuery authorization;
    public CustomerLookupAdapter(IdentityQuery identities,AuthorizationQuery authorization) { this.identities = identities;this.authorization=authorization; }
    public Order.CustomerSnapshot byId(Long identityId) {
        var identity = identities.byId(identityId).orElseThrow();
        String role=authorization.roles(identity.id()).stream().sorted().findFirst().orElse("USER");
        return new Order.CustomerSnapshot(identity.id(), identity.name(), identity.email(), role);
    }
}

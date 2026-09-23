package br.edu.securitystore.sales.infra.integration;
import br.edu.securitystore.iam.identity.api.module.IdentityQuery;
import br.edu.securitystore.iam.authorization.api.module.AuthoritiesQuery;
import br.edu.securitystore.sales.core.port.CustomerLookup;
import br.edu.securitystore.sales.core.domain.Order;
import org.springframework.stereotype.Component;
@Component public class CustomerLookupAdapter implements CustomerLookup {
    private final IdentityQuery identities;
    private final AuthoritiesQuery authorization;
    public CustomerLookupAdapter(IdentityQuery identities,AuthoritiesQuery authorization) { this.identities = identities;this.authorization=authorization; }
    public Order.CustomerSnapshot byEmail(String email) {
        var identity = identities.byEmail(email).orElseThrow();
        String role=authorization.roles(identity.id()).stream().sorted().findFirst().orElse("USER");
        return new Order.CustomerSnapshot(identity.id(), identity.name(), identity.email(), role);
    }
}

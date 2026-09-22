package br.edu.securitystore.sales.infra.integration;
import br.edu.securitystore.iam.api.module.IdentityQuery;
import br.edu.securitystore.sales.core.port.CustomerLookup;
import br.edu.securitystore.sales.core.domain.Order;
import org.springframework.stereotype.Component;
@Component public class CustomerLookupAdapter implements CustomerLookup {
    private final IdentityQuery identities;
    public CustomerLookupAdapter(IdentityQuery identities) { this.identities = identities; }
    public Order.CustomerSnapshot byEmail(String email) {
        var identity = identities.findByEmail(email).orElseThrow();
        return new Order.CustomerSnapshot(identity.id(), identity.name(), identity.email(), identity.role());
    }
}

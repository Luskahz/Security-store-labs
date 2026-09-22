package br.edu.securitystore.sales.infra.integration;
import br.edu.securitystore.iam.api.module.IdentityQuery;
import br.edu.securitystore.sales.core.port.CustomerLookup;
import org.springframework.stereotype.Component;
@Component public class CustomerLookupAdapter implements CustomerLookup {
    private final IdentityQuery identities;
    public CustomerLookupAdapter(IdentityQuery identities) { this.identities = identities; }
    public Customer byEmail(String email) {
        var identity = identities.findByEmail(email).orElseThrow();
        return new Customer(identity.id(), identity.name(), identity.email(), identity.role());
    }
}

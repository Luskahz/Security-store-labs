package br.edu.securitystore.sales.core.port;
import br.edu.securitystore.sales.core.domain.Order;
public interface CustomerLookup {
    Order.CustomerSnapshot byId(Long identityId);
}

package br.edu.securitystore.sales.infra.integration;
import br.edu.securitystore.catalog.api.module.CatalogOperations;
import br.edu.securitystore.sales.core.port.ProductReservation;
import br.edu.securitystore.sales.core.domain.Order;
import org.springframework.stereotype.Component;
@Component public class ProductReservationAdapter implements ProductReservation {
    private final CatalogOperations catalog;
    public ProductReservationAdapter(CatalogOperations catalog) { this.catalog = catalog; }
    public Order.ProductSnapshot reserve(Long productId, int quantity) {
        var product = catalog.reserve(productId, quantity);
        return new Order.ProductSnapshot(product.id(), product.name(), product.description(), product.price(), product.stock());
    }
}

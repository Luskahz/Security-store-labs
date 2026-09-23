package br.edu.securitystore.sales.core.port;
import br.edu.securitystore.sales.core.domain.Order;
public interface ProductReservation {
    Order.ProductSnapshot reserve(Long productId, int quantity);
}

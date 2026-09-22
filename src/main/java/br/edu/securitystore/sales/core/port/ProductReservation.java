package br.edu.securitystore.sales.core.port;
import java.math.BigDecimal;
public interface ProductReservation {
    record ReservedProduct(Long id, String name, String description, BigDecimal price, int stock) {}
    ReservedProduct reserve(Long productId, int quantity);
}

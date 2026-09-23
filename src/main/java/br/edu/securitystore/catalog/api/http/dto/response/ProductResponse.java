package br.edu.securitystore.catalog.api.http.dto.response;
import br.edu.securitystore.catalog.core.domain.Product;

import java.math.BigDecimal;

public record ProductResponse(Long id, String name, String description, BigDecimal price, int stock) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(product.getId(), product.getName(), product.getDescription(), product.getPrice(), product.getStock());
    }
}

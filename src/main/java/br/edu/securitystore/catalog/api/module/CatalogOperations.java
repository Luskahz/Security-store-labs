package br.edu.securitystore.catalog.api.module;

import java.math.BigDecimal;

/** Operações de catálogo necessárias para vender um produto. */
public interface CatalogOperations {
    record ProductSnapshot(Long id, String name, String description, BigDecimal price, int stock) {}
    ProductSnapshot reserve(Long productId, int quantity);
}

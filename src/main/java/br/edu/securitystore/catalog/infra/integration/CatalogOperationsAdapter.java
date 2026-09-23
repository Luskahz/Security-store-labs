package br.edu.securitystore.catalog.infra.integration;

import br.edu.securitystore.catalog.api.module.CatalogOperations;

import br.edu.securitystore.catalog.core.repository.ProductRepository;
import org.springframework.stereotype.Component;

@Component
public class CatalogOperationsAdapter implements CatalogOperations {
    private final ProductRepository products;
    public CatalogOperationsAdapter(ProductRepository products) { this.products = products; }
    public ProductSnapshot reserve(Long productId, int quantity) {
        var product = products.findLockedById(productId).orElseThrow();
        product.removeStock(quantity);
        products.save(product);
        return new ProductSnapshot(product.getId(), product.getName(), product.getDescription(), product.getPrice(), product.getStock());
    }
}

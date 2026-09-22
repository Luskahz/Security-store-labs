package br.edu.securitystore.catalog.api.module;

import br.edu.securitystore.catalog.ProductRepository;
import org.springframework.stereotype.Component;

@Component
class CatalogOperationsAdapter implements CatalogOperations {
    private final ProductRepository products;
    CatalogOperationsAdapter(ProductRepository products) { this.products = products; }
    public ProductSnapshot reserve(Long productId, int quantity) {
        var product = products.findLockedById(productId).orElseThrow();
        product.removeStock(quantity);
        return new ProductSnapshot(product.getId(), product.getName(), product.getDescription(), product.getPrice(), product.getStock());
    }
}

package br.edu.securitystore.catalog.core.application;

import br.edu.securitystore.catalog.core.domain.Product;
import br.edu.securitystore.catalog.core.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CatalogService {
    private final ProductRepository products;
    public CatalogService(ProductRepository products) { this.products = products; }
    public List<Product> list() { return products.findAll(); }
    public Product create(String name, String description, BigDecimal price, int stock) {
        return products.save(new Product(null, name, description, price, stock));
    }
    public boolean delete(Long id) {
        if (!products.existsById(id)) return false;
        products.deleteById(id);
        return true;
    }
}

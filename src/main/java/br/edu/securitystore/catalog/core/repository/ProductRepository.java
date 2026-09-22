package br.edu.securitystore.catalog.core.repository;

import br.edu.securitystore.catalog.core.domain.Product;
import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    List<Product> findAll();
    Optional<Product> findLockedById(Long id);
    Product save(Product product);
    boolean existsById(Long id);
    void deleteById(Long id);
}

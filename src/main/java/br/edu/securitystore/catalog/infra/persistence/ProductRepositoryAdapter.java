package br.edu.securitystore.catalog.infra.persistence;

import br.edu.securitystore.catalog.core.domain.Product;
import br.edu.securitystore.catalog.core.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class ProductRepositoryAdapter implements ProductRepository {
    private final JpaProductRepository jpa;
    public ProductRepositoryAdapter(JpaProductRepository jpa){this.jpa=jpa;}
    public List<Product> findAll(){return jpa.findAll().stream().map(this::map).toList();}
    public Optional<Product> findLockedById(Long id){return jpa.findLockedById(id).map(this::map);}
    public Product save(Product product){return map(jpa.save(new ProductEntity(product.getId(),product.getName(),product.getDescription(),product.getPrice(),product.getStock())));}
    public boolean existsById(Long id){return jpa.existsById(id);}
    public void deleteById(Long id){jpa.deleteById(id);}
    private Product map(ProductEntity e){return new Product(e.id,e.name,e.description,e.price,e.stock);}
}

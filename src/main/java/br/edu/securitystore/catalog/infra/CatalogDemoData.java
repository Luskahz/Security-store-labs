package br.edu.securitystore.catalog.infra;

import br.edu.securitystore.catalog.core.domain.Product;
import br.edu.securitystore.catalog.core.repository.ProductRepository;
import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CatalogDemoData {
    @Bean CommandLineRunner seedProducts(ProductRepository products) {
        return args -> {
            products.save(new Product(null,"Teclado Lab","Produto fictício",new BigDecimal("149.90"),10));
            products.save(new Product(null,"Mouse Lab","Produto fictício",new BigDecimal("79.90"),20));
        };
    }
}

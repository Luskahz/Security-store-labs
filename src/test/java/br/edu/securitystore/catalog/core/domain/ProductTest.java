package br.edu.securitystore.catalog.core.domain;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ProductTest {
    @Test void rejectsOversellingWithoutChangingStock() {
        Product product = new Product(1L,"Teste","Mock",new BigDecimal("10.00"),2);
        assertThrows(IllegalArgumentException.class, () -> product.removeStock(3));
        assertEquals(2, product.getStock());
    }
}

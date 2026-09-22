package br.edu.securitystore.sales.core.domain;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class OrderTest {
    @Test void totalComesFromReservedProductAndPaymentIsRepeatable() {
        Order order = new Order(new Order.CustomerSnapshot(1L,"Cliente","c@lab.local","CUSTOMER"),
                new Order.ProductSnapshot(2L,"Produto","Mock",new BigDecimal("12.50"),5),2);
        assertEquals(new BigDecimal("25.00"), order.getTotal());
        order.pay(); order.pay();
        assertEquals(Order.PaymentStatus.PAID, order.getPaymentStatus());
    }
}

package br.edu.securitystore.sales.api.http.dto.response;
import br.edu.securitystore.sales.core.domain.Order;
import br.edu.securitystore.logistics.api.module.DeliveryQuery;

import java.math.BigDecimal;

public record OrderResponse(Long id, Customer customer, ProductSummary product, int quantity,
        BigDecimal total, Order.PaymentStatus paymentStatus, DeliveryQuery.DeliverySummary delivery) {
    public record Customer(Long id, String name, String email, String role) {}
    public record ProductSummary(Long id, String name, String description, BigDecimal price, int stock) {}

    public static OrderResponse from(Order order, DeliveryQuery.DeliverySummary delivery) {
        return new OrderResponse(order.getId(),
                new Customer(order.getCustomerId(), order.getCustomerName(), order.getCustomerEmail(), order.getCustomerRole()),
                new ProductSummary(order.getProductId(), order.getProductName(), order.getProductDescription(), order.getProductPrice(), order.getProductStockAtPurchase()),
                order.getQuantity(), order.getTotal(), order.getPaymentStatus(), delivery);
    }
}

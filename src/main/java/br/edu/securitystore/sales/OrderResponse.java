package br.edu.securitystore.sales;

import java.math.BigDecimal;

public record OrderResponse(Long id, Customer customer, ProductSummary product, int quantity,
        BigDecimal total, Order.PaymentStatus paymentStatus, Order.DeliveryStatus deliveryStatus) {
    public record Customer(Long id, String name, String email, String role) {}
    public record ProductSummary(Long id, String name, String description, BigDecimal price, int stock) {}

    public static OrderResponse from(Order order) {
        var customer = order.getCustomer();
        var product = order.getProduct();
        return new OrderResponse(order.getId(),
                new Customer(customer.getId(), customer.getName(), customer.getEmail(), customer.getRole()),
                new ProductSummary(product.getId(), product.getName(), product.getDescription(), product.getPrice(), product.getStock()),
                order.getQuantity(), order.getTotal(), order.getPaymentStatus(), order.getDeliveryStatus());
    }
}

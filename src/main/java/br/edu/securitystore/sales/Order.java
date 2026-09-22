package br.edu.securitystore.sales;

import br.edu.securitystore.sales.core.port.CustomerLookup;
import br.edu.securitystore.sales.core.port.ProductReservation;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity @Table(name = "customer_orders")
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private Long customerId;
    @Column(nullable = false) private String customerName;
    @Column(nullable = false) private String customerEmail;
    @Column(nullable = false) private String customerRole;
    @Column(nullable = false) private Long productId;
    @Column(nullable = false) private String productName;
    private String productDescription;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal productPrice;
    private int productStockAtPurchase;
    private int quantity;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal total;
    @Enumerated(EnumType.STRING) private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    @Enumerated(EnumType.STRING) private DeliveryStatus deliveryStatus = DeliveryStatus.PREPARING;

    protected Order() {}
    public Order(CustomerLookup.Customer customer, ProductReservation.ReservedProduct product, int quantity) {
        this.customerId = customer.id(); this.customerName = customer.name(); this.customerEmail = customer.email(); this.customerRole = customer.role();
        this.productId = product.id(); this.productName = product.name(); this.productDescription = product.description();
        this.productPrice = product.price(); this.productStockAtPurchase = product.stock(); this.quantity = quantity;
        this.total = product.price().multiply(BigDecimal.valueOf(quantity));
    }
    public Long getId() { return id; } public Long getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; } public String getCustomerEmail() { return customerEmail; }
    public String getCustomerRole() { return customerRole; } public Long getProductId() { return productId; }
    public String getProductName() { return productName; } public String getProductDescription() { return productDescription; }
    public BigDecimal getProductPrice() { return productPrice; } public int getProductStockAtPurchase() { return productStockAtPurchase; }
    public int getQuantity() { return quantity; } public BigDecimal getTotal() { return total; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; } public DeliveryStatus getDeliveryStatus() { return deliveryStatus; }
    public void pay() { if (paymentStatus == PaymentStatus.PENDING) paymentStatus = PaymentStatus.PAID; }
    public void setDeliveryStatus(DeliveryStatus status) { deliveryStatus = status; }
    public enum PaymentStatus { PENDING, PAID, REFUNDED }
    public enum DeliveryStatus { PREPARING, SHIPPED, DELIVERED, CANCELLED }
}

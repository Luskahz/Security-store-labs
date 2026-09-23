package br.edu.securitystore.sales.core.domain;

import java.math.BigDecimal;

public class Order {
    private final Long id;
    private final Long customerId;
    private final String customerName;
    private final String customerEmail;
    private final String customerRole;
    private final Long productId;
    private final String productName;
    private final String productDescription;
    private final BigDecimal productPrice;
    private final int productStockAtPurchase;
    private final int quantity;
    private final BigDecimal total;
    private PaymentStatus paymentStatus;
    private DeliveryStatus deliveryStatus;

    public record CustomerSnapshot(Long id,String name,String email,String role) {}
    public record ProductSnapshot(Long id,String name,String description,BigDecimal price,int stock) {}
    public Order(CustomerSnapshot customer, ProductSnapshot product, int quantity) {
        this(null,customer.id(),customer.name(),customer.email(),customer.role(),product.id(),product.name(),product.description(),
                product.price(),product.stock(),quantity,product.price().multiply(BigDecimal.valueOf(quantity)),PaymentStatus.PENDING,DeliveryStatus.PREPARING);
    }
    public Order(Long id,Long customerId,String customerName,String customerEmail,String customerRole,Long productId,String productName,
            String productDescription,BigDecimal productPrice,int productStockAtPurchase,int quantity,BigDecimal total,
            PaymentStatus paymentStatus,DeliveryStatus deliveryStatus) {
        this.id=id;this.customerId=customerId;this.customerName=customerName;this.customerEmail=customerEmail;this.customerRole=customerRole;
        this.productId=productId;this.productName=productName;this.productDescription=productDescription;this.productPrice=productPrice;
        this.productStockAtPurchase=productStockAtPurchase;this.quantity=quantity;this.total=total;
        this.paymentStatus=paymentStatus;this.deliveryStatus=deliveryStatus;
    }
    public Long getId(){return id;} public Long getCustomerId(){return customerId;} public String getCustomerName(){return customerName;}
    public String getCustomerEmail(){return customerEmail;} public String getCustomerRole(){return customerRole;} public Long getProductId(){return productId;}
    public String getProductName(){return productName;} public String getProductDescription(){return productDescription;}
    public BigDecimal getProductPrice(){return productPrice;} public int getProductStockAtPurchase(){return productStockAtPurchase;}
    public int getQuantity(){return quantity;} public BigDecimal getTotal(){return total;}
    public PaymentStatus getPaymentStatus(){return paymentStatus;} public DeliveryStatus getDeliveryStatus(){return deliveryStatus;}
    public void pay(){if(paymentStatus==PaymentStatus.PENDING)paymentStatus=PaymentStatus.PAID;}
    public void setDeliveryStatus(DeliveryStatus status){deliveryStatus=status;}
    public enum PaymentStatus{PENDING,PAID,REFUNDED} public enum DeliveryStatus{PREPARING,SHIPPED,DELIVERED,CANCELLED}
}

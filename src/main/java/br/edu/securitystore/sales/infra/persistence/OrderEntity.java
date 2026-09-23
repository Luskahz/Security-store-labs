package br.edu.securitystore.sales.infra.persistence;

import br.edu.securitystore.sales.core.domain.Order;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity @Table(name="customer_orders")
class OrderEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
    @Column(nullable=false) Long customerId;
    @Column(nullable=false) String customerName;
    @Column(nullable=false) String customerEmail;
    @Column(nullable=false) String customerRole;
    @Column(nullable=false) Long productId;
    @Column(nullable=false) String productName;
    String productDescription;
    @Column(nullable=false,precision=12,scale=2) BigDecimal productPrice;
    int productStockAtPurchase;
    int quantity;
    @Column(nullable=false,precision=12,scale=2) BigDecimal total;
    @Enumerated(EnumType.STRING) Order.PaymentStatus paymentStatus;
    @Enumerated(EnumType.STRING) Order.DeliveryStatus deliveryStatus;
    protected OrderEntity(){}
    OrderEntity(Order order){id=order.getId();customerId=order.getCustomerId();customerName=order.getCustomerName();customerEmail=order.getCustomerEmail();customerRole=order.getCustomerRole();
        productId=order.getProductId();productName=order.getProductName();productDescription=order.getProductDescription();productPrice=order.getProductPrice();
        productStockAtPurchase=order.getProductStockAtPurchase();quantity=order.getQuantity();total=order.getTotal();paymentStatus=order.getPaymentStatus();deliveryStatus=order.getDeliveryStatus();}
    Order toDomain(){return new Order(id,customerId,customerName,customerEmail,customerRole,productId,productName,productDescription,
        productPrice,productStockAtPurchase,quantity,total,paymentStatus,deliveryStatus);}
}

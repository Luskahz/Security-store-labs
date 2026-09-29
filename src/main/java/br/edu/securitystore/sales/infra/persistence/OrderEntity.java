package br.edu.securitystore.sales.infra.persistence;

import br.edu.securitystore.sales.core.domain.Order;
import jakarta.persistence.*;
import java.math.BigDecimal;
import br.edu.securitystore.platform.privacy.PiiProtection;

@Entity @Table(name="customer_orders")
class OrderEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
    @Column(nullable=false) Long customerId;
    @Column(nullable=false) String customerName;
    @Column(nullable=false) String customerEmail;
    @Column(nullable=false) String customerRole;
    @Column(length=1024) String deliveryPhone;
    @Column(length=1024) String deliveryStreet;
    @Column(length=1024) String deliveryNumber;
    @Column(length=1024) String deliveryComplement;
    @Column(length=1024) String deliveryNeighborhood;
    @Column(length=1024) String deliveryCity;
    @Column(length=1024) String deliveryState;
    @Column(length=1024) String deliveryPostalCode;
    @Column(nullable=false) Long productId;
    @Column(nullable=false) String productName;
    String productDescription;
    @Column(nullable=false,precision=12,scale=2) BigDecimal productPrice;
    int productStockAtPurchase;
    int quantity;
    @Column(nullable=false,precision=12,scale=2) BigDecimal total;
    @Enumerated(EnumType.STRING) Order.PaymentStatus paymentStatus;
    protected OrderEntity(){}
    OrderEntity(Order order,PiiProtection pii){id=order.getId();customerId=order.getCustomerId();customerName=order.getCustomerName();customerEmail=order.getCustomerEmail();customerRole=order.getCustomerRole();deliveryPhone=pii.encrypt(order.getDeliveryPhone());deliveryStreet=pii.encrypt(order.getDeliveryStreet());deliveryNumber=pii.encrypt(order.getDeliveryNumber());deliveryComplement=pii.encrypt(order.getDeliveryComplement());deliveryNeighborhood=pii.encrypt(order.getDeliveryNeighborhood());deliveryCity=pii.encrypt(order.getDeliveryCity());deliveryState=pii.encrypt(order.getDeliveryState());deliveryPostalCode=pii.encrypt(order.getDeliveryPostalCode());
        productId=order.getProductId();productName=order.getProductName();productDescription=order.getProductDescription();productPrice=order.getProductPrice();
        productStockAtPurchase=order.getProductStockAtPurchase();quantity=order.getQuantity();total=order.getTotal();paymentStatus=order.getPaymentStatus();}
    Order toDomain(PiiProtection pii){return new Order(id,customerId,customerName,customerEmail,customerRole,productId,productName,productDescription,
        productPrice,productStockAtPurchase,quantity,total,paymentStatus,pii.decrypt(deliveryPhone),pii.decrypt(deliveryStreet),pii.decrypt(deliveryNumber),pii.decrypt(deliveryComplement),pii.decrypt(deliveryNeighborhood),pii.decrypt(deliveryCity),pii.decrypt(deliveryState),pii.decrypt(deliveryPostalCode));}
}

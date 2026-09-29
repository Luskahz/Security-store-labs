package br.edu.securitystore.sales.core.domain;

import java.math.BigDecimal;

public class Order {
    private final Long id;
    private final Long customerId;
    private final String customerName;
    private final String customerEmail;
    private final String customerRole;
    private final String deliveryPhone, deliveryStreet, deliveryNumber, deliveryComplement, deliveryNeighborhood, deliveryCity, deliveryState, deliveryPostalCode;
    private final Long productId;
    private final String productName;
    private final String productDescription;
    private final BigDecimal productPrice;
    private final int productStockAtPurchase;
    private final int quantity;
    private final BigDecimal total;
    private PaymentStatus paymentStatus;

    public record CustomerSnapshot(Long id,String name,String email,String role,String phone,String street,String number,String complement,String neighborhood,String city,String state,String postalCode,boolean profileComplete) {
        public CustomerSnapshot(Long id,String name,String email,String role){this(id,name,email,role,null,null,null,null,null,null,null,null,false);}
    }
    public record ProductSnapshot(Long id,String name,String description,BigDecimal price,int stock) {}
    public Order(CustomerSnapshot customer, ProductSnapshot product, int quantity) {
        this(null,customer.id(),customer.name(),customer.email(),customer.role(),product.id(),product.name(),product.description(),
                product.price(),product.stock(),quantity,product.price().multiply(BigDecimal.valueOf(quantity)),PaymentStatus.PENDING,
                customer.phone(),customer.street(),customer.number(),customer.complement(),customer.neighborhood(),customer.city(),customer.state(),customer.postalCode());
    }
    public Order(Long id,Long customerId,String customerName,String customerEmail,String customerRole,Long productId,String productName,
            String productDescription,BigDecimal productPrice,int productStockAtPurchase,int quantity,BigDecimal total,
            PaymentStatus paymentStatus) {
        this(id,customerId,customerName,customerEmail,customerRole,productId,productName,productDescription,productPrice,productStockAtPurchase,quantity,total,paymentStatus,null,null,null,null,null,null,null,null);
    }
    public Order(Long id,Long customerId,String customerName,String customerEmail,String customerRole,Long productId,String productName,
            String productDescription,BigDecimal productPrice,int productStockAtPurchase,int quantity,BigDecimal total,
            PaymentStatus paymentStatus,String deliveryPhone,String deliveryStreet,String deliveryNumber,String deliveryComplement,
            String deliveryNeighborhood,String deliveryCity,String deliveryState,String deliveryPostalCode) {
        this.id=id;this.customerId=customerId;this.customerName=customerName;this.customerEmail=customerEmail;this.customerRole=customerRole;
        this.productId=productId;this.productName=productName;this.productDescription=productDescription;this.productPrice=productPrice;
        this.productStockAtPurchase=productStockAtPurchase;this.quantity=quantity;this.total=total;
        this.paymentStatus=paymentStatus;
        this.deliveryPhone=deliveryPhone;this.deliveryStreet=deliveryStreet;this.deliveryNumber=deliveryNumber;this.deliveryComplement=deliveryComplement;
        this.deliveryNeighborhood=deliveryNeighborhood;this.deliveryCity=deliveryCity;this.deliveryState=deliveryState;this.deliveryPostalCode=deliveryPostalCode;
    }
    public Long getId(){return id;} public Long getCustomerId(){return customerId;} public String getCustomerName(){return customerName;}
    public String getCustomerEmail(){return customerEmail;} public String getCustomerRole(){return customerRole;} public Long getProductId(){return productId;}
    public String getProductName(){return productName;} public String getProductDescription(){return productDescription;}
    public BigDecimal getProductPrice(){return productPrice;} public int getProductStockAtPurchase(){return productStockAtPurchase;}
    public int getQuantity(){return quantity;} public BigDecimal getTotal(){return total;}
    public PaymentStatus getPaymentStatus(){return paymentStatus;}
    public String getDeliveryPhone(){return deliveryPhone;} public String getDeliveryStreet(){return deliveryStreet;} public String getDeliveryNumber(){return deliveryNumber;} public String getDeliveryComplement(){return deliveryComplement;} public String getDeliveryNeighborhood(){return deliveryNeighborhood;} public String getDeliveryCity(){return deliveryCity;} public String getDeliveryState(){return deliveryState;} public String getDeliveryPostalCode(){return deliveryPostalCode;}
    public void pay(){if(paymentStatus==PaymentStatus.PENDING)paymentStatus=PaymentStatus.PAID;}
    public enum PaymentStatus{PENDING,PAID,REFUNDED}
}

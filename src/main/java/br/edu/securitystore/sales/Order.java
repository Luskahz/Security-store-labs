package br.edu.securitystore.sales;
import br.edu.securitystore.catalog.Product; import br.edu.securitystore.iam.UserAccount; import jakarta.persistence.*; import java.math.BigDecimal;
@Entity @Table(name="customer_orders") public class Order {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @ManyToOne(optional=false) private UserAccount customer; @ManyToOne(optional=false) private Product product; private int quantity; @Column(precision=12,scale=2) private BigDecimal total; @Enumerated(EnumType.STRING) private PaymentStatus paymentStatus=PaymentStatus.PENDING; @Enumerated(EnumType.STRING) private DeliveryStatus deliveryStatus=DeliveryStatus.PREPARING;
 protected Order(){} public Order(UserAccount c,Product p,int q){customer=c;product=p;quantity=q;total=p.getPrice().multiply(BigDecimal.valueOf(q));}
 public Long getId(){return id;} public UserAccount getCustomer(){return customer;} public Product getProduct(){return product;} public int getQuantity(){return quantity;} public BigDecimal getTotal(){return total;} public PaymentStatus getPaymentStatus(){return paymentStatus;} public DeliveryStatus getDeliveryStatus(){return deliveryStatus;} public void pay(){paymentStatus=PaymentStatus.PAID;} public void setDeliveryStatus(DeliveryStatus s){deliveryStatus=s;}
 public enum PaymentStatus{PENDING,PAID,REFUNDED} public enum DeliveryStatus{PREPARING,SHIPPED,DELIVERED,CANCELLED}
}

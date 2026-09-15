package br.edu.securitystore.catalog;
import jakarta.persistence.*; import java.math.BigDecimal;
@Entity public class Product {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @Column(nullable=false) private String name; private String description; @Column(nullable=false,precision=12,scale=2) private BigDecimal price; @Column(nullable=false) private int stock;
 protected Product(){} public Product(String n,String d,BigDecimal p,int s){name=n;description=d;price=p;stock=s;}
 public Long getId(){return id;} public String getName(){return name;} public String getDescription(){return description;} public BigDecimal getPrice(){return price;} public int getStock(){return stock;}
 public void removeStock(int quantity){if(quantity<1||stock<quantity)throw new IllegalArgumentException("Estoque insuficiente");stock-=quantity;}
}

package br.edu.securitystore.catalog.infra.persistence;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity @Table(name="product")
class ProductEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
    @Column(nullable=false) String name;
    String description;
    @Column(nullable=false,precision=12,scale=2) BigDecimal price;
    @Column(nullable=false) int stock;
    protected ProductEntity() {}
    ProductEntity(Long id,String name,String description,BigDecimal price,int stock){this.id=id;this.name=name;this.description=description;this.price=price;this.stock=stock;}
}

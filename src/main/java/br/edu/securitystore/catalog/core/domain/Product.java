package br.edu.securitystore.catalog.core.domain;

import java.math.BigDecimal;

public class Product {
    private final Long id;
    private final String name;
    private final String description;
    private final BigDecimal price;
    private int stock;
    public Product(Long id, String name, String description, BigDecimal price, int stock) {
        this.id=id; this.name=name; this.description=description; this.price=price; this.stock=stock;
    }
    public Long getId(){return id;} public String getName(){return name;} public String getDescription(){return description;}
    public BigDecimal getPrice(){return price;} public int getStock(){return stock;}
    public void removeStock(int quantity){if(quantity<1||stock<quantity)throw new IllegalArgumentException("Estoque insuficiente");stock-=quantity;}
}

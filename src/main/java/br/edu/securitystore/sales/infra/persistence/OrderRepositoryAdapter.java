package br.edu.securitystore.sales.infra.persistence;

import br.edu.securitystore.sales.core.domain.Order;
import br.edu.securitystore.sales.core.repository.OrderRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class OrderRepositoryAdapter implements OrderRepository {
    private final JpaOrderRepository jpa;
    public OrderRepositoryAdapter(JpaOrderRepository jpa){this.jpa=jpa;}
    public List<Order> findAll(){return jpa.findAll().stream().map(OrderEntity::toDomain).toList();}
    public List<Order> findByCustomerId(Long id){return jpa.findByCustomerId(id).stream().map(OrderEntity::toDomain).toList();}
    public Optional<Order> findById(Long id){return jpa.findById(id).map(OrderEntity::toDomain);}
    public Order save(Order order){return jpa.save(new OrderEntity(order)).toDomain();}
}

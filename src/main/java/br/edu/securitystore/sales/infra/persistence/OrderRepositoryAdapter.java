package br.edu.securitystore.sales.infra.persistence;

import br.edu.securitystore.sales.core.domain.Order;
import br.edu.securitystore.sales.core.repository.OrderRepository;
import br.edu.securitystore.platform.privacy.PiiProtection;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class OrderRepositoryAdapter implements OrderRepository {
    private final JpaOrderRepository jpa; private final PiiProtection pii;
    public OrderRepositoryAdapter(JpaOrderRepository jpa,PiiProtection pii){this.jpa=jpa;this.pii=pii;}
    public List<Order> findAll(){return jpa.findAll().stream().map(e->e.toDomain(pii)).toList();}
    public List<Order> findByCustomerId(Long id){return jpa.findByCustomerId(id).stream().map(e->e.toDomain(pii)).toList();}
    public Optional<Order> findById(Long id){return jpa.findById(id).map(e->e.toDomain(pii));}
    public Order save(Order order){return jpa.save(new OrderEntity(order,pii)).toDomain(pii);}
}

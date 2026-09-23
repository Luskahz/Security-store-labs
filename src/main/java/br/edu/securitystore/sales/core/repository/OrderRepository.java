package br.edu.securitystore.sales.core.repository;

import br.edu.securitystore.sales.core.domain.Order;
import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    List<Order> findAll();
    List<Order> findByCustomerId(Long customerId);
    Optional<Order> findById(Long id);
    Order save(Order order);
}

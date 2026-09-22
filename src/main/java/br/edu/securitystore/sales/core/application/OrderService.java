package br.edu.securitystore.sales.core.application;

import br.edu.securitystore.sales.core.domain.Order;
import br.edu.securitystore.sales.core.repository.OrderRepository;
import br.edu.securitystore.sales.core.port.CustomerLookup;
import br.edu.securitystore.sales.core.port.ProductReservation;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private final OrderRepository orders;
    private final ProductReservation products;
    private final CustomerLookup customers;
    public OrderService(OrderRepository orders, ProductReservation products, CustomerLookup customers) {
        this.orders = orders; this.products = products; this.customers = customers;
    }
    @Transactional
    public List<Order> list(String email, boolean admin) {
        return admin ? orders.findAll() : orders.findByCustomerId(customers.byEmail(email).id());
    }
    @Transactional
    public Order create(String email, Long productId, int quantity) {
        var product = products.reserve(productId, quantity);
        var customer = customers.byEmail(email);
        return orders.save(new Order(customer, product, quantity));
    }
    @Transactional
    public Order pay(String email, Long id) {
        Order order = owned(email, id); order.pay(); return orders.save(order);
    }
    @Transactional
    public Order updateDelivery(Long id, Order.DeliveryStatus status) {
        Order order = orders.findById(id).orElseThrow(); order.setDeliveryStatus(status); return orders.save(order);
    }
    private Order owned(String email, Long id) {
        Order order = orders.findById(id).orElseThrow();
        if (!order.getCustomerId().equals(customers.byEmail(email).id())) throw new AccessDeniedException("Pedido de outro usuário");
        return order;
    }
}

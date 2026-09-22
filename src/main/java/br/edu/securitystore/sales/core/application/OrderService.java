package br.edu.securitystore.sales.core.application;

import br.edu.securitystore.catalog.*;
import br.edu.securitystore.iam.*;
import br.edu.securitystore.sales.*;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private final OrderRepository orders;
    private final ProductRepository products;
    private final UserRepository users;
    public OrderService(OrderRepository orders, ProductRepository products, UserRepository users) {
        this.orders = orders; this.products = products; this.users = users;
    }
    @Transactional
    public List<OrderResponse> list(String email, boolean admin) {
        return (admin ? orders.findAll() : orders.findByCustomerEmailIgnoreCase(email)).stream().map(OrderResponse::from).toList();
    }
    @Transactional
    public OrderResponse create(String email, Long productId, int quantity) {
        Product product = products.findById(productId).orElseThrow();
        product.removeStock(quantity);
        UserAccount customer = users.findByEmailIgnoreCase(email).orElseThrow();
        return OrderResponse.from(orders.save(new Order(customer, product, quantity)));
    }
    @Transactional
    public OrderResponse pay(String email, Long id) {
        Order order = owned(email, id); order.pay(); return OrderResponse.from(orders.save(order));
    }
    @Transactional
    public OrderResponse updateDelivery(Long id, Order.DeliveryStatus status) {
        Order order = orders.findById(id).orElseThrow(); order.setDeliveryStatus(status); return OrderResponse.from(orders.save(order));
    }
    private Order owned(String email, Long id) {
        Order order = orders.findById(id).orElseThrow();
        if (!order.getCustomer().getEmail().equalsIgnoreCase(email)) throw new AccessDeniedException("Pedido de outro usuário");
        return order;
    }
}

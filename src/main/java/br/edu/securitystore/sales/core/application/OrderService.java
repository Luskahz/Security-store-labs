package br.edu.securitystore.sales.core.application;

import br.edu.securitystore.sales.core.domain.Order;
import br.edu.securitystore.sales.core.repository.OrderRepository;
import br.edu.securitystore.sales.core.port.CustomerLookup;
import br.edu.securitystore.sales.core.port.ProductReservation;
import br.edu.securitystore.logistics.api.module.DeliveryOperations;
import br.edu.securitystore.logistics.api.module.DeliveryQuery;
import br.edu.securitystore.sales.api.http.dto.response.OrderResponse;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private final OrderRepository orders;
    private final ProductReservation products;
    private final CustomerLookup customers;
    private final DeliveryOperations deliveries;
    private final DeliveryQuery deliveryQuery;
    public OrderService(OrderRepository orders, ProductReservation products, CustomerLookup customers,
            DeliveryOperations deliveries, DeliveryQuery deliveryQuery) {
        this.orders = orders; this.products = products; this.customers = customers;
        this.deliveries = deliveries; this.deliveryQuery = deliveryQuery;
    }
    @Transactional
    public List<OrderResponse> list(Long identityId, boolean admin) {
        return (admin ? orders.findAll() : orders.findByCustomerId(identityId)).stream()
                .map(this::response).toList();
    }
    @Transactional
    public OrderResponse create(Long identityId, Long productId, int quantity) {
        var product = products.reserve(productId, quantity);
        var customer = customers.byId(identityId);
        Order order = orders.save(new Order(customer, product, quantity));
        deliveries.createForOrder(order.getId());
        return response(order);
    }
    @Transactional
    public OrderResponse pay(Long identityId, Long id) {
        Order order = owned(identityId, id); order.pay(); return response(orders.save(order));
    }
    @Transactional
    private OrderResponse response(Order order) {
        DeliveryQuery.DeliverySummary delivery = deliveryQuery.findByOrderId(order.getId())
                .orElseGet(() -> deliveries.createForOrder(order.getId()));
        return OrderResponse.from(order, delivery);
    }
    private Order owned(Long identityId, Long id) {
        Order order = orders.findById(id).orElseThrow();
        if (!order.getCustomerId().equals(identityId)) throw new AccessDeniedException("Pedido de outro usuário");
        return order;
    }
}

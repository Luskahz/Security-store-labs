package br.edu.securitystore.sales;
import br.edu.securitystore.sales.core.application.OrderService; import jakarta.validation.Valid; import jakarta.validation.constraints.*; import java.security.Principal; import java.util.List; import org.springframework.http.*; import org.springframework.security.core.Authentication; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/orders") public class OrderController {
 private final OrderService service; public OrderController(OrderService service){this.service=service;}
 public record CreateOrder(@NotNull Long productId,@Min(1) int quantity){} public record PaymentRequest(@NotBlank String method){} public record DeliveryRequest(@NotNull Order.DeliveryStatus status){}
 @GetMapping public List<OrderResponse> list(Authentication a){boolean admin=a.getAuthorities().stream().anyMatch(x->x.getAuthority().equals("ROLE_ADMIN"));return service.list(a.getName(),admin);}
 @PostMapping public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrder r,Principal principal){return ResponseEntity.status(HttpStatus.CREATED).body(service.create(principal.getName(),r.productId(),r.quantity()));}
 @PostMapping("/{id}/pay") public OrderResponse pay(@PathVariable Long id,Principal principal,@Valid @RequestBody PaymentRequest ignored){return service.pay(principal.getName(),id);}
 @PatchMapping("/{id}/delivery") public OrderResponse delivery(@PathVariable Long id,@Valid @RequestBody DeliveryRequest r){return service.updateDelivery(id,r.status());}
}

package br.edu.securitystore.sales.api.http.controller;
import br.edu.securitystore.sales.core.application.OrderService; import br.edu.securitystore.sales.api.http.dto.response.OrderResponse; import br.edu.securitystore.platform.security.UserPrincipal; import jakarta.validation.Valid; import jakarta.validation.constraints.*; import java.util.List; import org.springframework.http.*; import org.springframework.security.core.Authentication; import org.springframework.security.core.annotation.AuthenticationPrincipal; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/orders") public class OrderController {
 private final OrderService service; public OrderController(OrderService service){this.service=service;}
 public record CreateOrder(@NotNull Long productId,@Min(1) int quantity){} public record PaymentRequest(@NotBlank String method){}
 @GetMapping public List<OrderResponse> list(@AuthenticationPrincipal UserPrincipal principal,Authentication a){boolean admin=a.getAuthorities().stream().anyMatch(x->x.getAuthority().equals("SALES_ORDER_READ_ALL"));return service.list(principal.identityId(),admin);}
 @PostMapping public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrder r,@AuthenticationPrincipal UserPrincipal principal){return ResponseEntity.status(HttpStatus.CREATED).body(service.create(principal.identityId(),r.productId(),r.quantity()));}
 @PostMapping("/{id}/pay") public OrderResponse pay(@PathVariable Long id,@AuthenticationPrincipal UserPrincipal principal,@Valid @RequestBody PaymentRequest ignored){return service.pay(principal.identityId(),id);}
}

package br.edu.securitystore.catalog;
import br.edu.securitystore.catalog.core.application.CatalogService; import jakarta.validation.Valid; import jakarta.validation.constraints.*; import java.math.BigDecimal; import java.util.List; import org.springframework.http.*; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/products") public class ProductController {
 private final CatalogService service; public ProductController(CatalogService service){this.service=service;}
 public record ProductRequest(@NotBlank String name,String description,@NotNull @DecimalMin("0.01") BigDecimal price,@Min(0) int stock){}
 @GetMapping public List<ProductResponse> list(){return service.list().stream().map(ProductResponse::from).toList();}
 @PostMapping public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(ProductResponse.from(service.create(r.name(),r.description(),r.price(),r.stock())));}
 @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id){if(!service.delete(id))return ResponseEntity.notFound().build();return ResponseEntity.noContent().build();}
}

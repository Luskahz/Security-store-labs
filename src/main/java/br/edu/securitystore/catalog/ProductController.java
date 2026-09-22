package br.edu.securitystore.catalog;
import jakarta.validation.Valid; import jakarta.validation.constraints.*; import java.math.BigDecimal; import java.util.List; import org.springframework.http.*; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/products") public class ProductController {
 private final ProductRepository products; public ProductController(ProductRepository p){products=p;}
 public record ProductRequest(@NotBlank String name,String description,@NotNull @DecimalMin("0.01") BigDecimal price,@Min(0) int stock){}
 @GetMapping public List<ProductResponse> list(){return products.findAll().stream().map(ProductResponse::from).toList();}
 @PostMapping public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(ProductResponse.from(products.save(new Product(r.name(),r.description(),r.price(),r.stock()))));}
 @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id){if(!products.existsById(id))return ResponseEntity.notFound().build();products.deleteById(id);return ResponseEntity.noContent().build();}
}

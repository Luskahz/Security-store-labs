package br.edu.securitystore.config;
import br.edu.securitystore.catalog.*; import br.edu.securitystore.iam.*; import java.math.BigDecimal; import org.springframework.boot.CommandLineRunner; import org.springframework.context.annotation.*; import org.springframework.security.crypto.password.PasswordEncoder;
@Configuration public class DataInitializer {
 @Bean CommandLineRunner seed(UserRepository users,ProductRepository products,PasswordEncoder encoder){return args->{
  users.save(new UserAccount("Admin do Lab","admin@lab.local",encoder.encode("Admin123!"),"ADMIN")); users.save(new UserAccount("Aluno Demo","aluno@lab.local",encoder.encode("Aluno123!"),"CUSTOMER"));
  products.save(new Product("Teclado Lab","Produto fictício",new BigDecimal("149.90"),10)); products.save(new Product("Mouse Lab","Produto fictício",new BigDecimal("79.90"),20));
 };}
}

package br.edu.securitystore.iam.identity.infra;

import br.edu.securitystore.iam.identity.core.application.IdentityService;
import br.edu.securitystore.iam.identity.core.repository.IdentityRepository;
import br.edu.securitystore.iam.authorization.api.module.AuthoritiesQuery;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class IdentityBootstrap {
 @Bean @Order(2) CommandLineRunner bootstrapAdmin(IdentityService identities,IdentityRepository repository,AuthoritiesQuery authorization,
   @Value("${security.bootstrap.admin.enabled:false}") boolean enabled,@Value("${security.bootstrap.admin.name:}") String name,
   @Value("${security.bootstrap.admin.email:}") String email,@Value("${security.bootstrap.admin.password:}") String password,
   @Value("${security.demo.enabled:false}") boolean demo){
  return args->{
   if(enabled){if(name.isBlank()||email.isBlank()||password.length()<8)throw new IllegalStateException("Configure ADMIN_NAME, ADMIN_EMAIL e ADMIN_PASSWORD (mínimo 8 caracteres)");
    var admin=repository.byEmail(email).orElseGet(()->identities.create(name,email,password));
    authorization.assignAdmin(admin.id());}
   if(demo&&repository.byEmail("aluno@lab.local").isEmpty())identities.create("Aluno Demo","aluno@lab.local","Aluno123!");
  };
 }
}

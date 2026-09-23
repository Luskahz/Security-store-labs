package br.edu.securitystore.iam.authorization.infra;

import br.edu.securitystore.iam.authorization.core.application.AuthorizationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class AuthorizationBootstrap {
 @Bean @Order(1) CommandLineRunner seedAuthorization(AuthorizationService service){return args->service.bootstrap();}
}

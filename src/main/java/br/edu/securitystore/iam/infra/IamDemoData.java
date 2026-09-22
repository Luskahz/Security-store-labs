package br.edu.securitystore.iam.infra;

import br.edu.securitystore.iam.core.domain.UserAccount;
import br.edu.securitystore.iam.core.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class IamDemoData {
    @Bean CommandLineRunner seedUsers(UserRepository users, PasswordEncoder encoder) {
        return args -> {
            users.save(new UserAccount(null,"Admin do Lab","admin@lab.local",encoder.encode("Admin123!"),"ADMIN"));
            users.save(new UserAccount(null,"Aluno Demo","aluno@lab.local",encoder.encode("Aluno123!"),"CUSTOMER"));
        };
    }
}

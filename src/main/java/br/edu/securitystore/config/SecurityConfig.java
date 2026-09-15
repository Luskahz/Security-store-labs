package br.edu.securitystore.config;

import br.edu.securitystore.iam.UserRepository;
import org.springframework.context.annotation.*;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;

@Configuration @EnableMethodSecurity
public class SecurityConfig {
    @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
    @Bean UserDetailsService userDetailsService(UserRepository users){return email->{var u=users.findByEmailIgnoreCase(email).orElseThrow(()->new UsernameNotFoundException("Usuário não encontrado"));return User.withUsername(u.getEmail()).password(u.getPasswordHash()).roles(u.getRole()).build();};}
    @Bean SecurityFilterChain security(HttpSecurity http) throws Exception {
        return http.csrf(csrf->csrf.disable()).authorizeHttpRequests(auth->auth
            .requestMatchers("/","/index.html","/styles.css","/app.js","/api/iam/register","/api/products").permitAll()
            .requestMatchers(HttpMethod.POST,"/api/products/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE,"/api/products/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PATCH,"/api/orders/**").hasRole("ADMIN")
            .anyRequest().authenticated()).httpBasic(Customizer.withDefaults()).build();
    }
}

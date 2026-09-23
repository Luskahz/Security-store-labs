package br.edu.securitystore.platform.security;

import br.edu.securitystore.iam.authentication.api.module.AuthenticationOperations;
import br.edu.securitystore.iam.authorization.api.module.AuthoritiesQuery;
import br.edu.securitystore.iam.identity.api.module.IdentityQuery;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

@Configuration @EnableMethodSecurity
public class SecurityConfig {
 @Bean FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration(JwtAuthenticationFilter filter){var registration=new FilterRegistrationBean<>(filter);registration.setEnabled(false);return registration;}
 @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
 @Bean UserDetailsService userDetailsService(IdentityQuery identities,AuthenticationOperations accounts,AuthoritiesQuery authorization){
  return email->{var identity=identities.byEmail(email).filter(IdentityQuery.IdentityView::active).orElseThrow(()->new UsernameNotFoundException("Conta indisponível"));
   var credential=accounts.credential(identity.id()).filter(AuthenticationOperations.Credential::enabled).orElseThrow(()->new UsernameNotFoundException("Conta indisponível"));
   var authorities=authorization.authorities(identity.id()).stream().map(SimpleGrantedAuthority::new).toList();
   return new User(identity.email(),credential.passwordHash(),authorities);};
 }
 @Bean SecurityFilterChain security(HttpSecurity http,JwtAuthenticationFilter jwt) throws Exception {
  return http.csrf(csrf->csrf.disable()).sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
   .authorizeHttpRequests(auth->auth
    .requestMatchers("/","/index.html","/styles.css","/app.js","/login.html","/css/**","/js/**","/admin/*.html","/api/iam/register","/auth/login","/auth/refresh").permitAll()
    .requestMatchers(HttpMethod.GET,"/api/products").permitAll()
    .requestMatchers(HttpMethod.POST,"/api/products/**").hasRole("ADMIN")
    .requestMatchers(HttpMethod.DELETE,"/api/products/**").hasRole("ADMIN")
    .requestMatchers(HttpMethod.PATCH,"/api/orders/**").hasRole("ADMIN")
    .anyRequest().authenticated())
   .httpBasic(Customizer.withDefaults()).addFilterBefore(jwt,UsernamePasswordAuthenticationFilter.class).build();
 }
}

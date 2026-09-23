package br.edu.securitystore.platform.security;

import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
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
 @Bean SecurityFilterChain security(HttpSecurity http,JwtAuthenticationFilter jwt) throws Exception {
  return http.csrf(csrf->csrf.disable()).sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
   .authorizeHttpRequests(auth->auth
    .requestMatchers("/","/index.html","/styles.css","/app.js","/login.html","/css/**","/js/**","/admin/*.html","/auth/login","/auth/refresh").permitAll()
    .requestMatchers(HttpMethod.GET,"/api/products").permitAll()
    .requestMatchers(HttpMethod.POST,"/api/products/**").hasAuthority("CATALOG_PRODUCT_WRITE")
    .requestMatchers(HttpMethod.DELETE,"/api/products/**").hasAuthority("CATALOG_PRODUCT_WRITE")
    .requestMatchers(HttpMethod.PATCH,"/api/orders/**").hasAuthority("SALES_DELIVERY_UPDATE")
    .anyRequest().authenticated())
   .exceptionHandling(ex->ex.authenticationEntryPoint((request,response,error)->response.sendError(401))
    .accessDeniedHandler((request,response,error)->response.sendError(403)))
   .addFilterBefore(jwt,UsernamePasswordAuthenticationFilter.class).build();
 }
}

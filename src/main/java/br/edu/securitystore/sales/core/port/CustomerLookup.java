package br.edu.securitystore.sales.core.port;
public interface CustomerLookup {
    record Customer(Long id, String name, String email, String role) {}
    Customer byEmail(String email);
}

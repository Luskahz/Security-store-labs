package br.edu.securitystore.iam;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class UserAccount {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private String name;
    @Column(nullable = false, unique = true) private String email;
    @Column(nullable = false) private String passwordHash;
    @Column(nullable = false) private String role = "CUSTOMER";
    protected UserAccount() {}
    public UserAccount(String name, String email, String passwordHash, String role) { this.name=name; this.email=email; this.passwordHash=passwordHash; this.role=role; }
    public Long getId(){return id;} public String getName(){return name;} public String getEmail(){return email;} public String getPasswordHash(){return passwordHash;} public String getRole(){return role;}
}

package br.edu.securitystore.iam.infra.persistence;

import jakarta.persistence.*;

@Entity @Table(name = "users")
public class UserEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
    @Column(nullable = false) String name;
    @Column(nullable = false, unique = true) String email;
    @Column(nullable = false) String passwordHash;
    @Column(nullable = false) String role;
    protected UserEntity() {}
    public UserEntity(Long id, String name, String email, String passwordHash, String role) {
        this.id=id; this.name=name; this.email=email; this.passwordHash=passwordHash; this.role=role;
    }
}

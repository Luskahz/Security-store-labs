package br.edu.securitystore.iam.identity.infra.persistence;

import br.edu.securitystore.iam.identity.core.domain.Identity;
import jakarta.persistence.*;
import java.time.Instant;

@Entity @Table(name="identities",uniqueConstraints=@UniqueConstraint(columnNames="email"))
class IdentityEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
    @Column(nullable=false) String name;
    @Column(nullable=false) String email;
    @Enumerated(EnumType.STRING) @Column(nullable=false) Identity.Status status;
    @Column(nullable=false) Instant createdAt;
    @Column(nullable=false) Instant updatedAt;
    protected IdentityEntity() {}
    IdentityEntity(Identity d) { id=d.id();name=d.name();email=d.email();status=d.status();createdAt=d.createdAt();updatedAt=d.updatedAt(); }
    Identity toDomain() { return new Identity(id,name,email,status,createdAt,updatedAt); }
}

package br.edu.securitystore.iam.authorization.infra.persistence;
import br.edu.securitystore.iam.authorization.core.domain.AuthorizationModels.*;
import jakarta.persistence.*;import java.time.Instant;
@Entity @Table(name="roles",uniqueConstraints=@UniqueConstraint(columnNames="name"))
class RoleEntity {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;@Column(nullable=false) String name;String description;
 @Enumerated(EnumType.STRING) @Column(nullable=false) RoleStatus status;Instant createdAt;Instant updatedAt;
 protected RoleEntity(){} RoleEntity(Role r){id=r.id();name=r.name();description=r.description();status=r.status();createdAt=r.createdAt();updatedAt=r.updatedAt();}
 Role domain(){return new Role(id,name,description,status,createdAt,updatedAt);}
}

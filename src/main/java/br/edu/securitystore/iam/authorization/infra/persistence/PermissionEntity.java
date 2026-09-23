package br.edu.securitystore.iam.authorization.infra.persistence;
import br.edu.securitystore.iam.authorization.core.domain.AuthorizationModels.Permission;
import jakarta.persistence.*;import java.time.Instant;
@Entity @Table(name="permissions",uniqueConstraints=@UniqueConstraint(columnNames="code"))
class PermissionEntity {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;@Column(nullable=false) String code;String description;Instant createdAt;
 protected PermissionEntity(){} PermissionEntity(String code){this.code=code;this.description=code.replace('_',' ');this.createdAt=Instant.now();}
 Permission domain(){return new Permission(id,code,description,createdAt);}
}

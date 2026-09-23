package br.edu.securitystore.iam.authorization.infra.persistence;
import jakarta.persistence.*;import java.time.Instant;
@Entity @Table(name="role_permissions",uniqueConstraints=@UniqueConstraint(columnNames={"roleId","permissionId"}))
class RolePermissionEntity {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;@Column(nullable=false) Long roleId;@Column(nullable=false) Long permissionId;Instant createdAt;
 protected RolePermissionEntity(){}RolePermissionEntity(Long roleId,Long permissionId){this.roleId=roleId;this.permissionId=permissionId;this.createdAt=Instant.now();}
}

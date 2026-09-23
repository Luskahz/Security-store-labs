package br.edu.securitystore.iam.authorization.infra.persistence;
import jakarta.persistence.*;import java.time.Instant;
@Entity @Table(name="user_roles",uniqueConstraints=@UniqueConstraint(columnNames={"identityId","roleId"}))
class UserRoleEntity {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;@Column(nullable=false) Long identityId;@Column(nullable=false) Long roleId;Instant createdAt;
 protected UserRoleEntity(){}UserRoleEntity(Long identityId,Long roleId){this.identityId=identityId;this.roleId=roleId;this.createdAt=Instant.now();}
}

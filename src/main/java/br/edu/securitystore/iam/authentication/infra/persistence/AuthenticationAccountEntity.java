package br.edu.securitystore.iam.authentication.infra.persistence;
import br.edu.securitystore.iam.authentication.core.domain.AuthenticationAccount;
import jakarta.persistence.*;import java.time.Instant;
@Entity @Table(name="authentication_accounts") class AuthenticationAccountEntity {
 @Id Long identityId;@Column(nullable=false) String passwordHash;@Enumerated(EnumType.STRING) AuthenticationAccount.Status status;
 Instant lastLoginAt;Instant createdAt;Instant updatedAt;
 protected AuthenticationAccountEntity(){}AuthenticationAccountEntity(AuthenticationAccount a){identityId=a.identityId();passwordHash=a.passwordHash();status=a.status();lastLoginAt=a.lastLoginAt();createdAt=a.createdAt();updatedAt=a.updatedAt();}
 AuthenticationAccount domain(){return new AuthenticationAccount(identityId,passwordHash,status,lastLoginAt,createdAt,updatedAt);}
}

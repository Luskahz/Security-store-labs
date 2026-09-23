package br.edu.securitystore.iam.authentication.infra.persistence;
import br.edu.securitystore.iam.authentication.core.domain.Session;
import jakarta.persistence.*;import java.time.Instant;
@Entity @Table(name="auth_sessions") class SessionEntity {
 @Id String id;@Column(nullable=false) Long identityId;@Enumerated(EnumType.STRING) Session.Status status;
 Instant createdAt;Instant lastSeenAt;Instant expiresAt;Instant revokedAt;String ipAddress;String userAgent;
 protected SessionEntity(){}SessionEntity(Session s){id=s.id();identityId=s.identityId();status=s.status();createdAt=s.createdAt();lastSeenAt=s.lastSeenAt();expiresAt=s.expiresAt();revokedAt=s.revokedAt();ipAddress=s.ipAddress();userAgent=s.userAgent();}
 Session domain(){return new Session(id,identityId,status,createdAt,lastSeenAt,expiresAt,revokedAt,ipAddress,userAgent);}
}

package br.edu.securitystore.iam.authentication.infra.persistence;
import br.edu.securitystore.iam.authentication.core.domain.RefreshToken;
import jakarta.persistence.*;import java.time.Instant;
@Entity @Table(name="refresh_tokens",indexes=@Index(columnList="tokenHash",unique=true)) class RefreshTokenEntity {
 @Id String id;@Column(nullable=false) String sessionId;@Column(nullable=false) String tokenHash;@Enumerated(EnumType.STRING) RefreshToken.Status status;
 Instant createdAt;Instant expiresAt;Instant usedAt;Instant revokedAt;String replacedByTokenId;
 protected RefreshTokenEntity(){}RefreshTokenEntity(RefreshToken t){id=t.id();sessionId=t.sessionId();tokenHash=t.tokenHash();status=t.status();createdAt=t.createdAt();expiresAt=t.expiresAt();usedAt=t.usedAt();revokedAt=t.revokedAt();replacedByTokenId=t.replacedByTokenId();}
 RefreshToken domain(){return new RefreshToken(id,sessionId,tokenHash,status,createdAt,expiresAt,usedAt,revokedAt,replacedByTokenId);}
}

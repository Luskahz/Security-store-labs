package br.edu.securitystore.iam.authentication.core.domain;
import java.time.Instant;
public record RefreshToken(String id,String sessionId,String tokenHash,Status status,Instant createdAt,Instant expiresAt,Instant usedAt,Instant revokedAt,String replacedByTokenId){
 public enum Status{ACTIVE,ROTATED,REVOKED,EXPIRED}
 public boolean usable(Instant now){return status==Status.ACTIVE&&expiresAt.isAfter(now);}
 public RefreshToken rotated(String nextId,Instant now){return new RefreshToken(id,sessionId,tokenHash,Status.ROTATED,createdAt,expiresAt,now,revokedAt,nextId);}
 public RefreshToken revoked(Instant now){return new RefreshToken(id,sessionId,tokenHash,Status.REVOKED,createdAt,expiresAt,usedAt,now,replacedByTokenId);}
}

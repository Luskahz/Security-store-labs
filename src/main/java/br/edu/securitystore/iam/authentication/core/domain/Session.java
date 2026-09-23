package br.edu.securitystore.iam.authentication.core.domain;
import java.time.Instant;
public record Session(String id,Long identityId,Status status,Instant createdAt,Instant lastSeenAt,Instant expiresAt,Instant revokedAt,String ipAddress,String userAgent){
 public enum Status{ACTIVE,LOGGED_OUT,REVOKED,EXPIRED}
 public boolean active(Instant now){return status==Status.ACTIVE&&expiresAt.isAfter(now);}
 public Session withStatus(Status next,Instant now){return new Session(id,identityId,next,createdAt,lastSeenAt,expiresAt,now,ipAddress,userAgent);}
 public Session seen(Instant now){return new Session(id,identityId,status,createdAt,now,expiresAt,revokedAt,ipAddress,userAgent);}
}

package br.edu.securitystore.iam.authentication.core.domain;
import java.time.Instant;
public record AuthenticationAccount(Long identityId,String passwordHash,Status status,Instant lastLoginAt,Instant createdAt,Instant updatedAt){
 public enum Status{ENABLED,LOCKED}
 public AuthenticationAccount loggedIn(Instant now){return new AuthenticationAccount(identityId,passwordHash,status,now,createdAt,now);}
}

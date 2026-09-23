package br.edu.securitystore.iam.authentication.core.port;
import java.time.Instant;
public interface AccessTokenIssuer {String issue(Long identityId,String sessionId,Instant now);long expiresInSeconds();}

package br.edu.securitystore.iam.authentication.api.module;
public interface AuthenticationOperations {
 record Credential(String passwordHash,boolean enabled){}
 void createAccount(Long identityId,String password);
 void revokeAll(Long identityId);
 boolean active(Long identityId,String sessionId);
 boolean accountEnabled(Long identityId);
 java.util.Optional<Credential> credential(Long identityId);
}

package br.edu.securitystore.iam.authentication.api.module;
public interface AccessTokenVerifier {
 record VerifiedToken(Long identityId,String sessionId){}
 VerifiedToken verify(String token);
}

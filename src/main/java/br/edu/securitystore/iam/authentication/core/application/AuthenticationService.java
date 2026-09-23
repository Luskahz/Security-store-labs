package br.edu.securitystore.iam.authentication.core.application;

import br.edu.securitystore.iam.authentication.core.domain.*;
import br.edu.securitystore.iam.authentication.core.port.*;
import br.edu.securitystore.iam.authentication.core.repository.AuthenticationRepository;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.*;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationService {
 public record Tokens(String accessToken,String refreshToken,long expiresIn){}
 private final AuthenticationRepository repository;private final IdentityLookup identities;private final PasswordVerifier passwords;private final AccessTokenIssuer issuer;
 private final Duration refreshTtl;private final SecureRandom random=new SecureRandom();
 public AuthenticationService(AuthenticationRepository repository,IdentityLookup identities,PasswordVerifier passwords,AccessTokenIssuer issuer,
         @Value("${security.refresh-token-expiration:2592000}") long refreshSeconds){this.repository=repository;this.identities=identities;this.passwords=passwords;this.issuer=issuer;this.refreshTtl=Duration.ofSeconds(refreshSeconds);}
 @Transactional public void createAccount(Long identityId,String password){Instant now=Instant.now();repository.save(new AuthenticationAccount(identityId,passwords.hash(password),AuthenticationAccount.Status.ENABLED,null,now,now));}
 @Transactional public Tokens login(String email,String password,String ip,String agent){
  var identity=identities.byEmail(email).filter(IdentityLookup.Identity::active).orElseThrow(InvalidCredentialsException::new);
  var account=repository.account(identity.id()).filter(a->a.status()==AuthenticationAccount.Status.ENABLED).orElseThrow(InvalidCredentialsException::new);
  if(!passwords.matches(password,account.passwordHash()))throw new InvalidCredentialsException();
  Instant now=Instant.now();repository.save(account.loggedIn(now));
  Session session=repository.save(new Session(UUID.randomUUID().toString(),identity.id(),Session.Status.ACTIVE,now,now,now.plus(refreshTtl),null,ip,agent));
  return issue(session,now);
 }
 @Transactional public Tokens refresh(String raw){
  Instant now=Instant.now();RefreshToken old=repository.refreshByHashForUpdate(hash(raw)).orElseThrow(InvalidCredentialsException::new);
  if(!old.usable(now))throw new InvalidCredentialsException();
  Session session=repository.session(old.sessionId()).filter(s->s.active(now)).orElseThrow(InvalidCredentialsException::new);
  if(identities.byId(session.identityId()).filter(IdentityLookup.Identity::active).isEmpty()||!accountEnabled(session.identityId()))throw new InvalidCredentialsException();
  String replacementId=UUID.randomUUID().toString();repository.save(old.rotated(replacementId,now));
  return issue(session,now,replacementId);
 }
 @Transactional public void logout(Long identityId,String sessionId){revokeOwned(identityId,sessionId,Session.Status.LOGGED_OUT);}
 @Transactional public void revokeOwned(Long identityId,String sessionId,Session.Status status){
  Session session=repository.session(sessionId).filter(s->s.identityId().equals(identityId)).orElseThrow(NoSuchElementException::new);
  if(session.status()==Session.Status.ACTIVE)repository.save(session.withStatus(status,Instant.now()));
  repository.revokeActiveTokens(sessionId,Instant.now());
 }
 @Transactional public void revokeAll(Long identityId){for(Session s:repository.sessions(identityId))if(s.status()==Session.Status.ACTIVE)revokeOwned(identityId,s.id(),Session.Status.REVOKED);}
 @Transactional public void revokeOthers(Long identityId,String currentId){for(Session s:repository.sessions(identityId))if(!s.id().equals(currentId)&&s.status()==Session.Status.ACTIVE)revokeOwned(identityId,s.id(),Session.Status.REVOKED);}
 public List<Session> sessions(Long identityId){return repository.sessions(identityId);}
 public boolean accountEnabled(Long identityId){return repository.account(identityId).map(a->a.status()==AuthenticationAccount.Status.ENABLED).orElse(false);}
 public Optional<AuthenticationAccount> credential(Long identityId){return repository.account(identityId);}
 @Transactional public boolean active(Long identityId,String sessionId){
  Instant now=Instant.now();Session session=repository.session(sessionId).orElse(null);
  if(session==null||!session.identityId().equals(identityId)||!session.active(now)||!accountEnabled(identityId))return false;
  if(session.lastSeenAt()==null||session.lastSeenAt().isBefore(now.minusSeconds(30)))repository.save(session.seen(now));
  return true;
 }
 private Tokens issue(Session session,Instant now){return issue(session,now,UUID.randomUUID().toString());}
 private Tokens issue(Session session,Instant now,String tokenId){
  byte[] bytes=new byte[32];random.nextBytes(bytes);String raw=Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
  repository.save(new RefreshToken(tokenId,session.id(),hash(raw),RefreshToken.Status.ACTIVE,now,now.plus(refreshTtl),null,null,null));
  return new Tokens(issuer.issue(session.identityId(),session.id(),now),raw,issuer.expiresInSeconds());
 }
 private String hash(String raw){try{byte[] digest=MessageDigest.getInstance("SHA-256").digest(raw.getBytes(java.nio.charset.StandardCharsets.UTF_8));return HexFormat.of().formatHex(digest);}catch(java.security.NoSuchAlgorithmException e){throw new IllegalStateException(e);}}
 public static class InvalidCredentialsException extends RuntimeException {}
}

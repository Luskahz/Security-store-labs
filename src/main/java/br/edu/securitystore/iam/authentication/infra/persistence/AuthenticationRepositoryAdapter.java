package br.edu.securitystore.iam.authentication.infra.persistence;
import br.edu.securitystore.iam.authentication.core.domain.*;
import br.edu.securitystore.iam.authentication.core.repository.AuthenticationRepository;
import java.time.Instant;import java.util.*;import org.springframework.stereotype.Repository;
@Repository class AuthenticationRepositoryAdapter implements AuthenticationRepository {
 private final JpaAuthenticationAccountRepository accounts;private final JpaSessionRepository sessions;private final JpaRefreshTokenRepository tokens;
 AuthenticationRepositoryAdapter(JpaAuthenticationAccountRepository a,JpaSessionRepository s,JpaRefreshTokenRepository t){accounts=a;sessions=s;tokens=t;}
 public Optional<AuthenticationAccount> account(Long id){return accounts.findById(id).map(AuthenticationAccountEntity::domain);}
 public AuthenticationAccount save(AuthenticationAccount a){return accounts.save(new AuthenticationAccountEntity(a)).domain();}
 public Optional<Session> session(String id){return sessions.findById(id).map(SessionEntity::domain);}
 public List<Session> sessions(Long id){return sessions.findByIdentityId(id).stream().map(SessionEntity::domain).toList();}
 public Session save(Session s){return sessions.save(new SessionEntity(s)).domain();}
 public Optional<RefreshToken> refreshByHashForUpdate(String hash){return tokens.findByTokenHash(hash).map(RefreshTokenEntity::domain);}
 public RefreshToken save(RefreshToken t){return tokens.save(new RefreshTokenEntity(t)).domain();}
 public void revokeActiveTokens(String sessionId,Instant now){for(var t:tokens.findBySessionIdAndStatus(sessionId,RefreshToken.Status.ACTIVE))tokens.save(new RefreshTokenEntity(t.domain().revoked(now)));}
}

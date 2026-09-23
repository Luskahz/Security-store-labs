package br.edu.securitystore.iam.authentication.core.repository;
import br.edu.securitystore.iam.authentication.core.domain.*;import java.util.*;
public interface AuthenticationRepository {
 Optional<AuthenticationAccount> account(Long identityId);AuthenticationAccount save(AuthenticationAccount account);
 Optional<Session> session(String id);List<Session> sessions(Long identityId);Session save(Session session);
 Optional<RefreshToken> refreshByHashForUpdate(String hash);RefreshToken save(RefreshToken token);
 void revokeActiveTokens(String sessionId,java.time.Instant now);
}

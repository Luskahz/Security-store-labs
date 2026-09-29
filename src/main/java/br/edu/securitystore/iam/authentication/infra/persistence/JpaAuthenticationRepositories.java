package br.edu.securitystore.iam.authentication.infra.persistence;
import jakarta.persistence.LockModeType;import java.util.*;import org.springframework.data.jpa.repository.*;
interface JpaAuthenticationAccountRepository extends JpaRepository<AuthenticationAccountEntity,Long>{}
interface JpaSessionRepository extends JpaRepository<SessionEntity,String>{List<SessionEntity> findByIdentityId(Long identityId);}
interface JpaRefreshTokenRepository extends JpaRepository<RefreshTokenEntity,String>{
 @Lock(LockModeType.PESSIMISTIC_WRITE) Optional<RefreshTokenEntity> findByTokenHash(String hash);
 List<RefreshTokenEntity> findBySessionIdAndStatus(String sessionId,br.edu.securitystore.iam.authentication.core.domain.RefreshToken.Status status);
}
interface JpaPasswordResetTokenRepository extends JpaRepository<PasswordResetTokenEntity,Long>{
 @Lock(LockModeType.PESSIMISTIC_WRITE) Optional<PasswordResetTokenEntity> findByTokenHash(String tokenHash);
 Optional<PasswordResetTokenEntity> findTopByIdentityIdOrderByCreatedAtDesc(Long identityId);
 void deleteByIdentityId(Long identityId);
 void deleteByExpiresAtBefore(java.time.Instant instant);
}

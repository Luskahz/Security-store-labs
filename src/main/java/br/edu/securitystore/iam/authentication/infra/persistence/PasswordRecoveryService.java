package br.edu.securitystore.iam.authentication.infra.persistence;

import br.edu.securitystore.iam.authentication.core.application.AuthenticationService;
import br.edu.securitystore.iam.identity.api.module.IdentityQuery;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class PasswordRecoveryService {
    public static final String GENERIC_MESSAGE="Se o e-mail estiver cadastrado, enviaremos instruções para redefinir a senha.";
    private final JpaPasswordResetTokenRepository tokens;
    private final IdentityQuery identities;
    private final AuthenticationService authentication;
    private final ApplicationEventPublisher events;
    private final Duration ttl,cooldown;
    private final String from,resetUrl;
    private final SecureRandom random=new SecureRandom();
    private final Map<String,ArrayDeque<Instant>> requestsByAddress=new ConcurrentHashMap<>();

    public PasswordRecoveryService(JpaPasswordResetTokenRepository tokens,IdentityQuery identities,AuthenticationService authentication,ApplicationEventPublisher events,
            @Value("${security.auth.password-recovery-request-ttl:15m}") Duration ttl,
            @Value("${security.auth.password-recovery-cooldown:60s}") Duration cooldown,
            @Value("${app.mail.from:no-reply@localhost}") String from,
            @Value("${app.user-facing.password-reset-url:http://localhost:8080/password-reset.html}") String resetUrl){
        this.tokens=tokens;this.identities=identities;this.authentication=authentication;this.events=events;this.ttl=ttl;this.cooldown=cooldown;this.from=from;this.resetUrl=resetUrl;
    }

    @Transactional
    public void request(String email,String remoteAddress){
        if(rateLimited(remoteAddress))return;
        identities.byEmail(email.trim()).filter(IdentityQuery.IdentityView::active).ifPresent(identity->{
            Instant now=Instant.now();
            if(tokens.findTopByIdentityIdOrderByCreatedAtDesc(identity.id()).filter(t->t.createdAt.isAfter(now.minus(cooldown))).isPresent())return;
            tokens.deleteByExpiresAtBefore(now);
            tokens.deleteByIdentityId(identity.id());
            byte[] bytes=new byte[32];random.nextBytes(bytes);
            String raw=Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
            tokens.save(new PasswordResetTokenEntity(identity.id(),hash(raw),now,now.plus(ttl)));
            String link=UriComponentsBuilder.fromUriString(resetUrl).fragment("token="+raw).build().encode().toUriString();
            events.publishEvent(new PasswordRecoveryMailEvent(email.trim(),link,from,ttl.toMinutes()));
        });
    }

    private boolean rateLimited(String remoteAddress){
        Instant now=Instant.now();
        String key=remoteAddress==null?"unknown":remoteAddress;
        if(requestsByAddress.size()>=10000&&!requestsByAddress.containsKey(key))return true;
        ArrayDeque<Instant> attempts=requestsByAddress.computeIfAbsent(key,k->new ArrayDeque<>());
        synchronized(attempts){while(!attempts.isEmpty()&&attempts.peekFirst().isBefore(now.minus(Duration.ofMinutes(15))))attempts.removeFirst();if(attempts.size()>=5)return true;attempts.addLast(now);return false;}
    }

    @Transactional
    public void reset(String rawToken,String newPassword){
        PasswordResetTokenEntity token=tokens.findByTokenHash(hash(rawToken)).filter(t->t.expiresAt.isAfter(Instant.now())).orElseThrow(AuthenticationService.InvalidCredentialsException::new);
        authentication.resetPassword(token.identityId,newPassword);
        tokens.delete(token);
    }

    private String hash(String value){try{return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));}catch(NoSuchAlgorithmException e){throw new IllegalStateException(e);}}
}

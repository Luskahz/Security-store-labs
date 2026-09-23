package br.edu.securitystore.iam.identity.core.application;

import br.edu.securitystore.iam.identity.core.domain.Identity;
import br.edu.securitystore.iam.identity.core.port.*;
import br.edu.securitystore.iam.identity.core.repository.IdentityRepository;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IdentityService {
    private final IdentityRepository identities;
    private final AccountProvisioner accounts;
    private final DefaultRoleAssigner roles;
    private final SessionRevoker sessions;
    public IdentityService(IdentityRepository identities, AccountProvisioner accounts, DefaultRoleAssigner roles, SessionRevoker sessions) {
        this.identities=identities; this.accounts=accounts; this.roles=roles; this.sessions=sessions;
    }
    @Transactional
    public Identity create(String name, String email, String password) {
        String normalized = normalize(email);
        if (identities.byEmail(normalized).isPresent()) throw new DuplicateEmailException();
        Instant now=Instant.now();
        Identity identity=identities.save(new Identity(null,name.trim(),normalized,Identity.Status.ACTIVE,now,now));
        accounts.create(identity.id(),password);
        roles.assignUser(identity.id());
        return identity;
    }
    public Identity byId(Long id) { return identities.byId(id).orElseThrow(NoSuchElementException::new); }
    public Identity byEmail(String email) { return identities.byEmail(normalize(email)).orElseThrow(NoSuchElementException::new); }
    public List<Identity> all() { return identities.all(); }
    @Transactional
    public Identity update(Long id, String name, String email) {
        Identity current=byId(id);
        String normalized=normalize(email);
        if (!current.email().equals(normalized) && identities.byEmail(normalized).isPresent()) throw new DuplicateEmailException();
        return identities.save(current.rename(name.trim(),normalized,Instant.now()));
    }
    @Transactional
    public Identity setEnabled(Long id, boolean enabled) {
        Identity updated=identities.save(byId(id).withStatus(enabled?Identity.Status.ACTIVE:Identity.Status.DISABLED,Instant.now()));
        if (!enabled) sessions.revokeAll(id);
        return updated;
    }
    private String normalize(String email) { return email.trim().toLowerCase(java.util.Locale.ROOT); }
    public static class DuplicateEmailException extends RuntimeException {}
}

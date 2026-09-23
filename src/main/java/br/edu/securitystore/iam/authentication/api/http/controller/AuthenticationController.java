package br.edu.securitystore.iam.authentication.api.http.controller;

import br.edu.securitystore.iam.authentication.core.application.AuthenticationService;
import br.edu.securitystore.iam.authentication.core.domain.Session;
import br.edu.securitystore.iam.authorization.api.module.AuthorizationQuery;
import br.edu.securitystore.iam.identity.api.module.IdentityQuery;
import br.edu.securitystore.platform.security.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;
import java.util.Set;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/auth")
public class AuthenticationController {
 private final AuthenticationService service;private final IdentityQuery identities;private final AuthorizationQuery authorization;
 public AuthenticationController(AuthenticationService service,IdentityQuery identities,AuthorizationQuery authorization){this.service=service;this.identities=identities;this.authorization=authorization;}
 public record LoginRequest(@Email @NotBlank String email,@NotBlank String password){}
 public record RefreshRequest(@NotBlank String refreshToken){}
 public record Me(Long id,String name,String email,Set<String> roles,Set<String> permissions){}
 public record SessionView(String id,Session.Status status,java.time.Instant createdAt,java.time.Instant lastSeenAt,java.time.Instant expiresAt,String ipAddress,String userAgent,boolean current){
  static SessionView from(Session s,String current){var status=s.status()==Session.Status.ACTIVE&&!s.expiresAt().isAfter(java.time.Instant.now())?Session.Status.EXPIRED:s.status();return new SessionView(s.id(),status,s.createdAt(),s.lastSeenAt(),s.expiresAt(),s.ipAddress(),s.userAgent(),s.id().equals(current));}
 }
 @PostMapping("/login") public AuthenticationService.Tokens login(@Valid @RequestBody LoginRequest request,HttpServletRequest http){return service.login(request.email(),request.password(),http.getRemoteAddr(),http.getHeader("User-Agent"));}
 @PostMapping("/refresh") public AuthenticationService.Tokens refresh(@Valid @RequestBody RefreshRequest request){return service.refresh(request.refreshToken());}
 @PostMapping("/logout") public void logout(@AuthenticationPrincipal UserPrincipal principal){principal=required(principal);service.logout(principal.identityId(),principal.sessionId());}
 @GetMapping("/me") public Me me(@AuthenticationPrincipal UserPrincipal principal){principal=required(principal);var identity=identities.byId(principal.identityId()).orElseThrow();return new Me(identity.id(),identity.name(),identity.email(),authorization.roles(identity.id()),authorization.authorities(identity.id()).stream().filter(a->!a.startsWith("ROLE_")).collect(java.util.stream.Collectors.toCollection(java.util.TreeSet::new)));}
 @GetMapping("/me/sessions") public List<SessionView> sessions(@AuthenticationPrincipal UserPrincipal principal){principal=required(principal);String id=principal.sessionId();return service.sessions(principal.identityId()).stream().map(s->SessionView.from(s,id)).toList();}
 @DeleteMapping("/me/sessions/{sessionId}") public void revoke(@AuthenticationPrincipal UserPrincipal principal,@PathVariable String sessionId){principal=required(principal);service.revokeOwned(principal.identityId(),sessionId,Session.Status.REVOKED);}
 @DeleteMapping("/me/sessions") public void revokeOthers(@AuthenticationPrincipal UserPrincipal principal){principal=required(principal);service.revokeOthers(principal.identityId(),principal.sessionId());}
 private UserPrincipal required(UserPrincipal principal){if(principal==null)throw new InsufficientAuthenticationException("Use Bearer token");return principal;}
}

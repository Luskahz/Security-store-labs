package br.edu.securitystore.iam.authentication.api.http.controller;

import br.edu.securitystore.iam.authentication.core.application.AuthenticationService;
import br.edu.securitystore.iam.authentication.core.domain.Session;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/admin/users/{identityId}/sessions")
public class AdminSessionController {
 private final AuthenticationService service;public AdminSessionController(AuthenticationService service){this.service=service;}
 @GetMapping @PreAuthorize("hasAuthority('AUTHENTICATION_SESSION_READ')") public List<AuthenticationController.SessionView> list(@PathVariable Long identityId){return service.sessions(identityId).stream().map(s->AuthenticationController.SessionView.from(s,"")).toList();}
 @DeleteMapping("/{sessionId}") @PreAuthorize("hasAuthority('AUTHENTICATION_SESSION_REVOKE')") public void revoke(@PathVariable Long identityId,@PathVariable String sessionId){service.revokeOwned(identityId,sessionId,Session.Status.REVOKED);}
 @DeleteMapping @PreAuthorize("hasAuthority('AUTHENTICATION_SESSION_REVOKE')") public void revokeAll(@PathVariable Long identityId){service.revokeAll(identityId);}
}

package br.edu.securitystore.iam.identity.api.http.controller;

import br.edu.securitystore.iam.identity.core.application.IdentityService;
import br.edu.securitystore.iam.identity.core.domain.Identity;
import br.edu.securitystore.iam.authorization.api.module.AuthorizationQuery;
import jakarta.validation.Valid;import jakarta.validation.constraints.*;import java.util.*;
import org.springframework.http.*;import org.springframework.security.access.prepost.PreAuthorize;import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/admin/users")
public class AdminIdentityController {
 private final IdentityService identities;private final AuthorizationQuery roles;
 public AdminIdentityController(IdentityService identities,AuthorizationQuery roles){this.identities=identities;this.roles=roles;}
 public record CreateUser(@NotBlank String name,@Email @NotBlank String email,@NotBlank @Size(min=8) String password){}
 public record UpdateUser(@NotBlank String name,@Email @NotBlank String email){}
 public record UserView(Long id,String name,String email,Identity.Status status,java.time.Instant createdAt,java.time.Instant updatedAt,Set<String> roles){}
 private UserView view(Identity identity){return new UserView(identity.id(),identity.name(),identity.email(),identity.status(),identity.createdAt(),identity.updatedAt(),roles.roles(identity.id()));}
 @GetMapping @PreAuthorize("hasAuthority('IDENTITY_USER_READ')") public List<UserView> all(){return identities.all().stream().map(this::view).toList();}
 @GetMapping("/{id}") @PreAuthorize("hasAuthority('IDENTITY_USER_READ')") public UserView one(@PathVariable Long id){return view(identities.byId(id));}
 @PostMapping @PreAuthorize("hasAuthority('IDENTITY_USER_CREATE')") public ResponseEntity<UserView> create(@Valid @RequestBody CreateUser request){return ResponseEntity.status(HttpStatus.CREATED).body(view(identities.create(request.name(),request.email(),request.password())));}
 @PatchMapping("/{id}") @PreAuthorize("hasAuthority('IDENTITY_USER_UPDATE')") public UserView update(@PathVariable Long id,@Valid @RequestBody UpdateUser request){return view(identities.update(id,request.name(),request.email()));}
 @PatchMapping("/{id}/disable") @PreAuthorize("hasAuthority('IDENTITY_USER_DISABLE')") public UserView disable(@PathVariable Long id){return view(identities.setEnabled(id,false));}
 @PatchMapping("/{id}/enable") @PreAuthorize("hasAuthority('IDENTITY_USER_ENABLE')") public UserView enable(@PathVariable Long id){return view(identities.setEnabled(id,true));}
}

package br.edu.securitystore.iam.authorization.api.http.controller;

import br.edu.securitystore.iam.authorization.core.application.AuthorizationService;
import br.edu.securitystore.iam.authorization.core.domain.AuthorizationModels.*;
import br.edu.securitystore.iam.identity.api.module.IdentityQuery;
import jakarta.validation.Valid;import jakarta.validation.constraints.*;import java.util.*;
import org.springframework.http.*;import org.springframework.security.access.prepost.PreAuthorize;import org.springframework.web.bind.annotation.*;

@RestController
public class AdminAuthorizationController {
 private final AuthorizationService service;private final IdentityQuery identities;
 public AdminAuthorizationController(AuthorizationService service,IdentityQuery identities){this.service=service;this.identities=identities;}
 public record RoleRequest(@NotBlank String name,String description){}
 public record Ids(@NotNull Set<Long> permissionIds){}
 public record RoleIds(@NotNull Set<Long> roleIds){}
 @GetMapping("/admin/permissions") @PreAuthorize("hasAuthority('AUTHORIZATION_PERMISSION_READ')") public List<Permission> permissions(){return service.permissions();}
 @GetMapping("/admin/roles") @PreAuthorize("hasAuthority('AUTHORIZATION_ROLE_READ')") public List<Role> roles(){return service.roles();}
 @GetMapping("/admin/roles/{id}") @PreAuthorize("hasAuthority('AUTHORIZATION_ROLE_READ')") public Role role(@PathVariable Long id){return service.role(id);}
 @PostMapping("/admin/roles") @PreAuthorize("hasAuthority('AUTHORIZATION_ROLE_CREATE')") public ResponseEntity<Role> create(@Valid @RequestBody RoleRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(service.createRole(r.name(),r.description()));}
 @PatchMapping("/admin/roles/{id}") @PreAuthorize("hasAuthority('AUTHORIZATION_ROLE_UPDATE')") public Role update(@PathVariable Long id,@Valid @RequestBody RoleRequest r){return service.updateRole(id,r.name(),r.description());}
 @PatchMapping("/admin/roles/{id}/disable") @PreAuthorize("hasAuthority('AUTHORIZATION_ROLE_UPDATE')") public Role disable(@PathVariable Long id){return service.setRoleEnabled(id,false);}
 @PatchMapping("/admin/roles/{id}/enable") @PreAuthorize("hasAuthority('AUTHORIZATION_ROLE_UPDATE')") public Role enable(@PathVariable Long id){return service.setRoleEnabled(id,true);}
 @DeleteMapping("/admin/roles/{id}") @PreAuthorize("hasAuthority('AUTHORIZATION_ROLE_DELETE')") public void delete(@PathVariable Long id){service.deleteRole(id);}
 @GetMapping("/admin/roles/{id}/permissions") @PreAuthorize("hasAuthority('AUTHORIZATION_ROLE_READ')") public Set<Long> rolePermissions(@PathVariable Long id){return service.permissionIds(id);}
 @PutMapping("/admin/roles/{id}/permissions") @PreAuthorize("hasAuthority('AUTHORIZATION_ROLE_UPDATE')") public void replacePermissions(@PathVariable Long id,@Valid @RequestBody Ids request){service.replacePermissions(id,request.permissionIds());}
 @PutMapping("/admin/roles/{id}/permissions/{permissionId}") @PreAuthorize("hasAuthority('AUTHORIZATION_ROLE_UPDATE')") public void addPermission(@PathVariable Long id,@PathVariable Long permissionId){Set<Long> ids=new HashSet<>(service.permissionIds(id));ids.add(permissionId);service.replacePermissions(id,ids);}
 @DeleteMapping("/admin/roles/{id}/permissions/{permissionId}") @PreAuthorize("hasAuthority('AUTHORIZATION_ROLE_UPDATE')") public void removePermission(@PathVariable Long id,@PathVariable Long permissionId){Set<Long> ids=new HashSet<>(service.permissionIds(id));ids.remove(permissionId);service.replacePermissions(id,ids);}
 @GetMapping("/admin/users/{identityId}/roles") @PreAuthorize("hasAuthority('AUTHORIZATION_USER_ROLE_READ')") public List<Role> userRoles(@PathVariable Long identityId){requireIdentity(identityId);return service.rolesFor(identityId);}
 @PutMapping("/admin/users/{identityId}/roles") @PreAuthorize("hasAuthority('AUTHORIZATION_USER_ROLE_ASSIGN')") public void replaceUserRoles(@PathVariable Long identityId,@Valid @RequestBody RoleIds request){requireIdentity(identityId);service.replaceUserRoles(identityId,request.roleIds());}
 @PutMapping("/admin/users/{identityId}/roles/{roleId}") @PreAuthorize("hasAuthority('AUTHORIZATION_USER_ROLE_ASSIGN')") public void assign(@PathVariable Long identityId,@PathVariable Long roleId){requireIdentity(identityId);service.assignUserRole(identityId,roleId);}
 @DeleteMapping("/admin/users/{identityId}/roles/{roleId}") @PreAuthorize("hasAuthority('AUTHORIZATION_USER_ROLE_REVOKE')") public void revoke(@PathVariable Long identityId,@PathVariable Long roleId){requireIdentity(identityId);service.revokeUserRole(identityId,roleId);}
 private void requireIdentity(Long id){if(identities.byId(id).isEmpty())throw new NoSuchElementException();}
}

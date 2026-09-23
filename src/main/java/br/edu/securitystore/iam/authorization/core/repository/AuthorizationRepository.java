package br.edu.securitystore.iam.authorization.core.repository;

import br.edu.securitystore.iam.authorization.core.domain.Permission;
import br.edu.securitystore.iam.authorization.core.domain.Role;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface AuthorizationRepository {
    List<Permission> permissions();
    Optional<Permission> permission(Long id);
    List<Permission> permissionsByIds(Set<Long> ids);
    void ensurePermission(String code);
    List<Role> roles();
    Optional<Role> role(Long id);
    Optional<Role> roleByName(String name);
    Role saveRole(Role role);
    void deleteRole(Long id);
    Set<Long> permissionIds(Long roleId);
    void replacePermissions(Long roleId,Set<Long> ids);
    List<Role> rolesForIdentity(Long identityId);
    void replaceUserRoles(Long identityId,Set<Long> roleIds);
}

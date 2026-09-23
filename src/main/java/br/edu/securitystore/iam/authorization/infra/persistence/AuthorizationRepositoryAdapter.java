package br.edu.securitystore.iam.authorization.infra.persistence;

import br.edu.securitystore.iam.authorization.core.domain.AuthorizationModels.*;
import br.edu.securitystore.iam.authorization.core.repository.AuthorizationRepository;
import java.util.*;
import org.springframework.stereotype.Repository;

@Repository
class AuthorizationRepositoryAdapter implements AuthorizationRepository {
 private final JpaRoleRepository roles;private final JpaPermissionRepository permissions;private final JpaRolePermissionRepository rolePermissions;private final JpaUserRoleRepository userRoles;
 AuthorizationRepositoryAdapter(JpaRoleRepository roles,JpaPermissionRepository permissions,JpaRolePermissionRepository rolePermissions,JpaUserRoleRepository userRoles){this.roles=roles;this.permissions=permissions;this.rolePermissions=rolePermissions;this.userRoles=userRoles;}
 public List<Permission> permissions(){return permissions.findAll().stream().map(PermissionEntity::domain).toList();}
 public Optional<Permission> permission(Long id){return permissions.findById(id).map(PermissionEntity::domain);}
 public List<Permission> permissionsByIds(Set<Long> ids){return permissions.findAllById(ids).stream().map(PermissionEntity::domain).toList();}
 public void ensurePermission(String code){if(permissions.findByCode(code).isEmpty())permissions.save(new PermissionEntity(code));}
 public List<Role> roles(){return roles.findAll().stream().map(RoleEntity::domain).toList();}
 public Optional<Role> role(Long id){return roles.findById(id).map(RoleEntity::domain);}
 public Optional<Role> roleByName(String name){return roles.findByName(name).map(RoleEntity::domain);}
 public Role saveRole(Role role){return roles.save(new RoleEntity(role)).domain();}
 public void deleteRole(Long id){rolePermissions.deleteByRoleId(id);userRoles.deleteByRoleId(id);roles.deleteById(id);}
 public Set<Long> permissionIds(Long id){Set<Long> result=new HashSet<>();for(var rp:rolePermissions.findByRoleId(id))result.add(rp.permissionId);return result;}
 public void replacePermissions(Long roleId,Set<Long> ids){rolePermissions.deleteByRoleId(roleId);rolePermissions.flush();for(Long id:ids)rolePermissions.save(new RolePermissionEntity(roleId,id));}
 public List<Role> rolesForIdentity(Long id){Set<Long> ids=new HashSet<>();for(var ur:userRoles.findByIdentityId(id))ids.add(ur.roleId);return roles.findAllById(ids).stream().map(RoleEntity::domain).toList();}
 public void replaceUserRoles(Long id,Set<Long> ids){userRoles.deleteByIdentityId(id);userRoles.flush();for(Long roleId:ids)userRoles.save(new UserRoleEntity(id,roleId));}
}

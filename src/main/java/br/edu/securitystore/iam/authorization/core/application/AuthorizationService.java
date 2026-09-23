package br.edu.securitystore.iam.authorization.core.application;

import br.edu.securitystore.iam.authorization.core.domain.AuthorizationModels.*;
import br.edu.securitystore.iam.authorization.core.domain.PermissionCode;
import br.edu.securitystore.iam.authorization.core.repository.AuthorizationRepository;
import java.time.Instant;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthorizationService {
    private final AuthorizationRepository repository;
    public AuthorizationService(AuthorizationRepository repository){this.repository=repository;}
    public List<Permission> permissions(){return repository.permissions();}
    public List<Role> roles(){return repository.roles();}
    public Role role(Long id){return repository.role(id).orElseThrow(NoSuchElementException::new);}
    public Set<Long> permissionIds(Long roleId){role(roleId);return repository.permissionIds(roleId);}
    public List<Role> rolesFor(Long identityId){return repository.rolesForIdentity(identityId);}
    public Set<String> roleNames(Long identityId){Set<String> result=new TreeSet<>();for(Role role:rolesFor(identityId))if(role.status()==RoleStatus.ACTIVE)result.add(role.name());return result;}
    public Set<String> authorities(Long identityId){
        Set<String> result=new TreeSet<>();Set<Long> permissions=new HashSet<>();
        for(Role role:rolesFor(identityId))if(role.status()==RoleStatus.ACTIVE){
            result.add("ROLE_"+role.name());
            permissions.addAll(repository.permissionIds(role.id()));
        }
        for(Permission p:repository.permissionsByIds(permissions))result.add(p.code());
        return result;
    }
    @Transactional public Role createRole(String name,String description){
        String normalized=name.trim().toUpperCase(Locale.ROOT);
        if(repository.roleByName(normalized).isPresent())throw new IllegalArgumentException("Role já existe");
        Instant now=Instant.now();return repository.saveRole(new Role(null,normalized,description,RoleStatus.ACTIVE,now,now));
    }
    @Transactional public Role updateRole(Long id,String name,String description){
        Role old=role(id);String normalized=name.trim().toUpperCase(Locale.ROOT);
        if(old.name().equals("ADMIN")&&!normalized.equals("ADMIN"))throw new IllegalArgumentException("Role ADMIN é estrutural");
        if(!old.name().equals(normalized)&&repository.roleByName(normalized).isPresent())throw new IllegalArgumentException("Role já existe");
        return repository.saveRole(new Role(id,normalized,description,old.status(),old.createdAt(),Instant.now()));
    }
    @Transactional public Role setRoleEnabled(Long id,boolean enabled){
        Role old=role(id);if(old.name().equals("ADMIN")&&!enabled)throw new IllegalArgumentException("Role ADMIN é estrutural");
        return repository.saveRole(new Role(id,old.name(),old.description(),enabled?RoleStatus.ACTIVE:RoleStatus.DISABLED,old.createdAt(),Instant.now()));
    }
    @Transactional public void deleteRole(Long id){if(role(id).name().equals("ADMIN"))throw new IllegalArgumentException("Role ADMIN é estrutural");repository.deleteRole(id);}
    @Transactional public void replacePermissions(Long roleId,Set<Long> ids){
        Role role=role(roleId);if(role.name().equals("ADMIN"))throw new IllegalArgumentException("Permissions de ADMIN são geridas pelo bootstrap");
        for(Long id:ids)if(repository.permission(id).isEmpty())throw new NoSuchElementException();
        repository.replacePermissions(roleId,ids);
    }
    @Transactional public void replaceUserRoles(Long identityId,Set<Long> ids){for(Long id:ids)role(id);repository.replaceUserRoles(identityId,ids);}
    @Transactional public void assignUserRole(Long identityId,Long roleId){Set<Long> ids=new HashSet<>();for(Role role:rolesFor(identityId))ids.add(role.id());ids.add(role(roleId).id());repository.replaceUserRoles(identityId,ids);}
    @Transactional public void revokeUserRole(Long identityId,Long roleId){Set<Long> ids=new HashSet<>();for(Role role:rolesFor(identityId))ids.add(role.id());ids.remove(roleId);repository.replaceUserRoles(identityId,ids);}
    @Transactional public void bootstrap(){
        for(PermissionCode code:PermissionCode.values())repository.ensurePermission(code.name());
        for(String name:List.of("ADMIN","MANAGER","USER"))if(repository.roleByName(name).isEmpty())createRole(name,name+" role");
        Role admin=repository.roleByName("ADMIN").orElseThrow();
        Set<Long> all=new HashSet<>();for(Permission p:repository.permissions())all.add(p.id());
        repository.replacePermissions(admin.id(),all);
    }
}

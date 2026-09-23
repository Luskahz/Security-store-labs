package br.edu.securitystore.iam.authorization.infra.persistence;
import java.util.*;import org.springframework.data.jpa.repository.JpaRepository;
interface JpaRoleRepository extends JpaRepository<RoleEntity,Long>{Optional<RoleEntity> findByName(String name);}
interface JpaPermissionRepository extends JpaRepository<PermissionEntity,Long>{Optional<PermissionEntity> findByCode(String code);}
interface JpaRolePermissionRepository extends JpaRepository<RolePermissionEntity,Long>{List<RolePermissionEntity> findByRoleId(Long roleId);void deleteByRoleId(Long roleId);}
interface JpaUserRoleRepository extends JpaRepository<UserRoleEntity,Long>{List<UserRoleEntity> findByIdentityId(Long identityId);void deleteByIdentityId(Long identityId);void deleteByRoleId(Long roleId);}

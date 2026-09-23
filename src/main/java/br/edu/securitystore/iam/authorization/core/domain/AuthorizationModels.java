package br.edu.securitystore.iam.authorization.core.domain;

import java.time.Instant;

public final class AuthorizationModels {
    private AuthorizationModels() {}
    public enum RoleStatus { ACTIVE, DISABLED }
    public record Role(Long id,String name,String description,RoleStatus status,Instant createdAt,Instant updatedAt) {}
    public record Permission(Long id,String code,String description,Instant createdAt) {}
    public record RolePermission(Long id,Long roleId,Long permissionId,Instant createdAt) {}
    public record UserRole(Long id,Long identityId,Long roleId,Instant createdAt) {}
}

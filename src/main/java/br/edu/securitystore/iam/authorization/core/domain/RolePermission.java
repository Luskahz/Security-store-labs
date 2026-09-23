package br.edu.securitystore.iam.authorization.core.domain;

import java.time.Instant;

public record RolePermission(Long id, Long roleId, Long permissionId, Instant createdAt) {}

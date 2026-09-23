package br.edu.securitystore.iam.authorization.core.domain;

import java.time.Instant;

public record UserRole(Long id, Long identityId, Long roleId, Instant createdAt) {}

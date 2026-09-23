package br.edu.securitystore.iam.authorization.core.domain;

import java.time.Instant;

public record Permission(Long id, String code, String description, Instant createdAt) {}

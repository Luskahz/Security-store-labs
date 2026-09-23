package br.edu.securitystore.iam.authorization.core.domain;

import java.time.Instant;

public record Role(Long id, String name, String description, Status status, Instant createdAt, Instant updatedAt) {
    public enum Status { ACTIVE, DISABLED }
}

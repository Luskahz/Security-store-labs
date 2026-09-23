package br.edu.securitystore.iam.identity.core.domain;

import java.time.Instant;

public record Identity(Long id, String name, String email, Status status, Instant createdAt, Instant updatedAt) {
    public enum Status { ACTIVE, DISABLED }
    public Identity rename(String name, String email, Instant now) { return new Identity(id, name, email, status, createdAt, now); }
    public Identity withStatus(Status status, Instant now) { return new Identity(id, name, email, status, createdAt, now); }
}

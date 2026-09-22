package br.edu.securitystore.iam.core.domain;

public record UserAccount(Long id, String name, String email, String passwordHash, String role) {}

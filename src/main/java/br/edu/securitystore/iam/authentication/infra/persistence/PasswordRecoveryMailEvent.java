package br.edu.securitystore.iam.authentication.infra.persistence;

public record PasswordRecoveryMailEvent(String email,String link,String from,long expiresInMinutes) {}

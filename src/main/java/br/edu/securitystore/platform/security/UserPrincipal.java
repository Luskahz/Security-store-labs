package br.edu.securitystore.platform.security;
public record UserPrincipal(Long identityId,String sessionId,String email) {}

package br.edu.securitystore.platform.security;
import java.security.Principal;
public record UserPrincipal(Long identityId,String sessionId,String email) implements Principal { public String getName(){return email;} }

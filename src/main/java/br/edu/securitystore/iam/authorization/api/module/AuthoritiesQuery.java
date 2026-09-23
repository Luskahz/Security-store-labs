package br.edu.securitystore.iam.authorization.api.module;
import java.util.Set;
public interface AuthoritiesQuery { Set<String> authorities(Long identityId); Set<String> roles(Long identityId); void assignUser(Long identityId); void assignAdmin(Long identityId); }

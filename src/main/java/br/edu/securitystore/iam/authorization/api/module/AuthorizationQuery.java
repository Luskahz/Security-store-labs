package br.edu.securitystore.iam.authorization.api.module;

import java.util.Set;

public interface AuthorizationQuery {
    Set<String> authorities(Long identityId);
    Set<String> roles(Long identityId);
}

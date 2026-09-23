package br.edu.securitystore.iam.authentication.core.port;
import java.util.Optional;
public interface IdentityLookup {
 record Identity(Long id,String name,String email,boolean active){}
 Optional<Identity> byEmail(String email);Optional<Identity> byId(Long id);
}

package br.edu.securitystore.iam.authentication.infra.integration;
import br.edu.securitystore.iam.authentication.core.port.PasswordVerifier;
import org.springframework.security.crypto.password.PasswordEncoder;import org.springframework.stereotype.Component;
@Component class PasswordVerifierAdapter implements PasswordVerifier {
 private final PasswordEncoder encoder;PasswordVerifierAdapter(PasswordEncoder encoder){this.encoder=encoder;}
 public boolean matches(String raw,String hash){return encoder.matches(raw,hash);}public String hash(String raw){return encoder.encode(raw);}
}

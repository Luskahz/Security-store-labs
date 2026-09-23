package br.edu.securitystore.iam.authentication.infra.security.jwt;

import br.edu.securitystore.iam.authentication.api.module.AccessTokenVerifier;
import br.edu.securitystore.iam.authentication.core.port.AccessTokenIssuer;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

@Component
public class JwtService implements AccessTokenIssuer,AccessTokenVerifier {
 private final JwtEncoder encoder;private final JwtDecoder decoder;private final String issuer;private final long ttl;
 public JwtService(@Value("${security.jwt.secret}") String encodedSecret,@Value("${security.jwt.issuer:store-api}") String issuer,
  @Value("${security.jwt.access-token-expiration:900}") long ttl){
  byte[] bytes;
  try{bytes=Base64.getDecoder().decode(encodedSecret);}catch(IllegalArgumentException e){throw new IllegalStateException("JWT_SECRET deve ser Base64 de pelo menos 32 bytes",e);}
  if(bytes.length<32||ttl<1)throw new IllegalStateException("JWT_SECRET deve ter pelo menos 256 bits e expiração positiva");
  var key=new SecretKeySpec(bytes,"HmacSHA256");
  encoder=new NimbusJwtEncoder(new ImmutableSecret<>(key));
  var nimbus=NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
  nimbus.setJwtValidator(JwtValidators.createDefaultWithIssuer(issuer));
  decoder=nimbus;this.issuer=issuer;this.ttl=ttl;
 }
 public String issue(Long identityId,String sessionId,Instant now){
  var claims=JwtClaimsSet.builder().issuer(issuer).subject(identityId.toString()).id(UUID.randomUUID().toString())
   .issuedAt(now).expiresAt(now.plusSeconds(ttl)).claim("sid",sessionId).build();
  return encoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(),claims)).getTokenValue();
 }
 public long expiresInSeconds(){return ttl;}
 public VerifiedToken verify(String raw){Jwt jwt=decoder.decode(raw);String sid=jwt.getClaimAsString("sid");
  if(sid==null||sid.isBlank()||jwt.getId()==null)throw new BadJwtException("JWT sem sessão ou identificador");
  try{return new VerifiedToken(Long.valueOf(jwt.getSubject()),sid);}catch(NumberFormatException e){throw new BadJwtException("JWT com subject inválido");}
 }
}

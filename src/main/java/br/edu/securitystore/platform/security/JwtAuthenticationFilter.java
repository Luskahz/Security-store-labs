package br.edu.securitystore.platform.security;

import br.edu.securitystore.iam.authentication.api.module.*;
import br.edu.securitystore.iam.authorization.api.module.AuthoritiesQuery;
import br.edu.securitystore.iam.identity.api.module.IdentityQuery;
import jakarta.servlet.*;import jakarta.servlet.http.*;import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
 private final AccessTokenVerifier tokens;private final AuthenticationOperations sessions;private final IdentityQuery identities;private final AuthoritiesQuery authorization;
 public JwtAuthenticationFilter(AccessTokenVerifier tokens,AuthenticationOperations sessions,IdentityQuery identities,AuthoritiesQuery authorization){this.tokens=tokens;this.sessions=sessions;this.identities=identities;this.authorization=authorization;}
 @Override protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain chain)throws ServletException,IOException{
  String header=request.getHeader("Authorization");
  if(header!=null&&header.startsWith("Bearer ")){
   try{
    var verified=tokens.verify(header.substring(7));
    var identity=identities.byId(verified.identityId()).filter(IdentityQuery.IdentityView::active).orElse(null);
    if(identity!=null&&sessions.active(verified.identityId(),verified.sessionId())){
     var principal=new UserPrincipal(identity.id(),verified.sessionId(),identity.email());
     var authorities=authorization.authorities(identity.id()).stream().map(SimpleGrantedAuthority::new).toList();
     SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(principal,null,authorities));
    }
   }catch(JwtException|IllegalArgumentException ignored){SecurityContextHolder.clearContext();}
  }
  chain.doFilter(request,response);
 }
}

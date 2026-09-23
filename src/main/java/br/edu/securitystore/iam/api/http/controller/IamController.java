package br.edu.securitystore.iam.api.http.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.security.Principal;
import org.springframework.http.*;
import br.edu.securitystore.iam.identity.core.application.IdentityService;
import br.edu.securitystore.iam.identity.core.domain.Identity;
import br.edu.securitystore.iam.authorization.api.module.AuthoritiesQuery;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/iam")
public class IamController {
    private final IdentityService service;
    private final AuthoritiesQuery authorization;
    public IamController(IdentityService service,AuthoritiesQuery authorization){this.service=service;this.authorization=authorization;}
    public record RegisterRequest(@NotBlank String name, @Email @NotBlank String email, @NotBlank @Size(min=8,max=72) String password){}
    public record UserResponse(Long id,String name,String email,String role){}
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest r){
        Identity u=service.create(r.name(),r.email(),r.password());
        return ResponseEntity.status(HttpStatus.CREATED).body(response(u));
    }
    @GetMapping("/me") public UserResponse me(Principal principal){ return response(service.byEmail(principal.getName())); }
    private UserResponse response(Identity u){String role=authorization.roles(u.id()).stream().sorted().findFirst().orElse("USER");return new UserResponse(u.id(),u.name(),u.email(),role);}
}

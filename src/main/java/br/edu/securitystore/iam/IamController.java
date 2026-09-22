package br.edu.securitystore.iam;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.security.Principal;
import org.springframework.http.*;
import br.edu.securitystore.iam.core.application.IamService;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/iam")
public class IamController {
    private final IamService service;
    public IamController(IamService service){this.service=service;}
    public record RegisterRequest(@NotBlank String name, @Email @NotBlank String email, @Size(min=8,max=72) String password){}
    public record UserResponse(Long id,String name,String email,String role){}
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest r){
        UserAccount u=service.register(r.name(),r.email(),r.password());
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserResponse(u.getId(),u.getName(),u.getEmail(),u.getRole()));
    }
    @GetMapping("/me") public UserResponse me(Principal principal){ UserAccount u=service.findByEmail(principal.getName()); return new UserResponse(u.getId(),u.getName(),u.getEmail(),u.getRole()); }
}

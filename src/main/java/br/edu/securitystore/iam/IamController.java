package br.edu.securitystore.iam;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.security.Principal;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/iam")
public class IamController {
    private final UserRepository users; private final PasswordEncoder encoder;
    public IamController(UserRepository users, PasswordEncoder encoder){this.users=users;this.encoder=encoder;}
    public record RegisterRequest(@NotBlank String name, @Email @NotBlank String email, @Size(min=8,max=72) String password){}
    public record UserResponse(Long id,String name,String email,String role){}
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest r){
        String email=r.email().trim().toLowerCase();
        if(users.existsByEmailIgnoreCase(email)) return ResponseEntity.status(HttpStatus.CONFLICT).body("E-mail já cadastrado");
        UserAccount u=users.save(new UserAccount(r.name().trim(),email,encoder.encode(r.password()),"CUSTOMER"));
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserResponse(u.getId(),u.getName(),u.getEmail(),u.getRole()));
    }
    @GetMapping("/me") public UserResponse me(Principal principal){ UserAccount u=users.findByEmailIgnoreCase(principal.getName()).orElseThrow(); return new UserResponse(u.getId(),u.getName(),u.getEmail(),u.getRole()); }
}

package br.edu.securitystore.iam.identity.api.http.controller;

import br.edu.securitystore.iam.identity.core.application.IdentityService;
import br.edu.securitystore.iam.identity.core.domain.Identity;
import br.edu.securitystore.platform.security.UserPrincipal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/identity/me/profile")
public class IdentityProfileController {
    public record ProfileRequest(@NotBlank @Size(min=11,max=14) String cpf,
            @NotBlank @Size(min=10,max=16) String phone,
            @NotBlank @Size(max=120) String street,@NotBlank @Size(max=20) String number,
            @Size(max=80) String complement,@NotBlank @Size(max=80) String neighborhood,
            @NotBlank @Size(max=80) String city,@NotBlank @Pattern(regexp="[A-Za-z]{2}") String state,
            @NotBlank @Size(min=8,max=9) String postalCode) {}
    public record ProfileView(String cpf,String phone,String street,String number,String complement,
            String neighborhood,String city,String state,String postalCode) {}
    private final IdentityService identities;
    public IdentityProfileController(IdentityService identities){this.identities=identities;}

    @GetMapping
    public ProfileView get(@AuthenticationPrincipal UserPrincipal principal){return view(identities.byId(principal.identityId()));}

    @PutMapping
    public ProfileView save(@AuthenticationPrincipal UserPrincipal principal,@Valid @RequestBody ProfileRequest request){
        return view(identities.updateProfile(principal.identityId(),request.cpf(),request.phone(),request.street(),request.number(),request.complement(),request.neighborhood(),request.city(),request.state(),request.postalCode()));
    }

    private ProfileView view(Identity i){return new ProfileView(i.cpf(),i.phone(),i.street(),i.number(),i.complement(),i.neighborhood(),i.city(),i.state(),i.postalCode());}
}

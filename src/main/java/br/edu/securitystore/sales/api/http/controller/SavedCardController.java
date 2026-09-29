package br.edu.securitystore.sales.api.http.controller;

import br.edu.securitystore.platform.security.UserPrincipal;
import br.edu.securitystore.sales.infra.persistence.SavedCardService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment-cards")
public class SavedCardController {
    public record AddCardRequest(@jakarta.validation.constraints.NotBlank @Pattern(regexp="[0-9][0-9 -]{10,21}[0-9]") String cardNumber) {}
    private final SavedCardService cards;
    public SavedCardController(SavedCardService cards) { this.cards=cards; }

    @GetMapping
    public List<SavedCardService.Card> list(@AuthenticationPrincipal UserPrincipal principal) {
        return cards.list(principal.identityId());
    }

    @PostMapping
    public SavedCardService.Card add(@Valid @RequestBody AddCardRequest request, @AuthenticationPrincipal UserPrincipal principal) {
        String digits=request.cardNumber().replaceAll("[ -]", "");
        if(digits.length()<12||digits.length()>19)throw new IllegalArgumentException("Informe de 12 a 19 dígitos");
        return cards.add(principal.identityId(),digits);
    }

    @DeleteMapping("/{token}")
    public void remove(@PathVariable String token, @AuthenticationPrincipal UserPrincipal principal) {
        cards.remove(principal.identityId(),token);
    }
}

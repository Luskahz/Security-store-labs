package br.edu.securitystore.sales.infra.persistence;

import java.time.Instant;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SavedCardService {
    public record Card(String token, String lastFour, Instant createdAt) {}
    private final JpaSavedCardRepository cards;
    public SavedCardService(JpaSavedCardRepository cards) { this.cards=cards; }

    @Transactional(readOnly=true)
    public List<Card> list(Long identityId) {
        return cards.findByIdentityIdOrderByCreatedAtDesc(identityId).stream()
                .map(card -> new Card(card.token,card.lastFour,card.createdAt)).toList();
    }

    @Transactional
    public Card add(Long identityId, String rawNumber) {
        String digits=rawNumber.replaceAll("[ -]", "");
        SavedCardEntity saved=cards.save(new SavedCardEntity(identityId,UUID.randomUUID().toString(),digits.substring(digits.length()-4),Instant.now()));
        return new Card(saved.token,saved.lastFour,saved.createdAt);
    }

    @Transactional
    public void remove(Long identityId, String token) { cards.deleteByIdentityIdAndToken(identityId,token); }

    @Transactional(readOnly=true)
    public boolean belongsTo(Long identityId, String token) { return cards.findByIdentityIdAndToken(identityId,token).isPresent(); }
}

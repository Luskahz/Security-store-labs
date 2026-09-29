package br.edu.securitystore.sales.infra.persistence;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name="saved_payment_cards", indexes=@Index(name="idx_saved_card_owner", columnList="identity_id"))
class SavedCardEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
    @Column(name="identity_id",nullable=false) Long identityId;
    @Column(nullable=false, unique=true, length=36) String token;
    @Column(nullable=false, length=4) String lastFour;
    @Column(nullable=false) Instant createdAt;
    protected SavedCardEntity() {}
    SavedCardEntity(Long identityId, String token, String lastFour, Instant createdAt) {
        this.identityId=identityId; this.token=token; this.lastFour=lastFour; this.createdAt=createdAt;
    }
}

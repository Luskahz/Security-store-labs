package br.edu.securitystore.iam.authentication.infra.persistence;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name="password_reset_tokens",indexes=@Index(name="idx_password_reset_identity",columnList="identity_id"))
class PasswordResetTokenEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
    @Column(name="identity_id",nullable=false) Long identityId;
    @Column(nullable=false,unique=true,length=64) String tokenHash;
    @Column(nullable=false) Instant createdAt;
    @Column(nullable=false) Instant expiresAt;
    protected PasswordResetTokenEntity() {}
    PasswordResetTokenEntity(Long identityId,String tokenHash,Instant createdAt,Instant expiresAt){this.identityId=identityId;this.tokenHash=tokenHash;this.createdAt=createdAt;this.expiresAt=expiresAt;}
}

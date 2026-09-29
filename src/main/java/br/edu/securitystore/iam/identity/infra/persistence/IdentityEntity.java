package br.edu.securitystore.iam.identity.infra.persistence;

import br.edu.securitystore.iam.identity.core.domain.Identity;
import jakarta.persistence.*;
import java.time.Instant;

@Entity @Table(name="identities",uniqueConstraints=@UniqueConstraint(columnNames="email"))
class IdentityEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
    @Column(nullable=false) String name;
    @Column(nullable=false) String email;
    @Enumerated(EnumType.STRING) @Column(nullable=false) Identity.Status status;
    @Column(nullable=false) Instant createdAt;
    @Column(nullable=false) Instant updatedAt;
    @Column(name="cpf_encrypted",length=256) String cpfEncrypted;
    @Column(name="cpf_fingerprint",unique=true,length=64) String cpfFingerprint;
    @Column(name="phone_encrypted",length=1024) String phoneEncrypted;
    @Column(name="street_encrypted",length=1024) String streetEncrypted;
    @Column(name="number_encrypted",length=1024) String numberEncrypted;
    @Column(name="complement_encrypted",length=1024) String complementEncrypted;
    @Column(name="neighborhood_encrypted",length=1024) String neighborhoodEncrypted;
    @Column(name="city_encrypted",length=1024) String cityEncrypted;
    @Column(name="state_encrypted",length=1024) String stateEncrypted;
    @Column(name="postal_code_encrypted",length=1024) String postalCodeEncrypted;
    protected IdentityEntity() {}
    IdentityEntity(Identity d) { id=d.id();name=d.name();email=d.email();status=d.status();createdAt=d.createdAt();updatedAt=d.updatedAt(); }
    Identity toDomain(String cpf,String phone,String street,String number,String complement,String neighborhood,String city,String state,String postalCode) { return new Identity(id,name,email,status,createdAt,updatedAt,cpf,phone,street,number,complement,neighborhood,city,state,postalCode); }
}

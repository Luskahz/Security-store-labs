package br.edu.securitystore.iam.identity.core.domain;

import java.time.Instant;

public record Identity(Long id, String name, String email, Status status, Instant createdAt, Instant updatedAt,
        String cpf, String phone, String street, String number, String complement, String neighborhood,
        String city, String state, String postalCode) {
    public Identity(Long id,String name,String email,Status status,Instant createdAt,Instant updatedAt) {
        this(id,name,email,status,createdAt,updatedAt,null,null,null,null,null,null,null,null,null);
    }
    public enum Status { ACTIVE, DISABLED }
    public Identity rename(String name, String email, Instant now) { return new Identity(id,name,email,status,createdAt,now,cpf,phone,street,number,complement,neighborhood,city,state,postalCode); }
    public Identity withStatus(Status status, Instant now) { return new Identity(id,name,email,status,createdAt,now,cpf,phone,street,number,complement,neighborhood,city,state,postalCode); }
    public Identity withProfile(String cpf,String phone,String street,String number,String complement,String neighborhood,String city,String state,String postalCode,Instant now) {
        return new Identity(id,name,email,status,createdAt,now,cpf,phone,street,number,complement,neighborhood,city,state,postalCode);
    }
}

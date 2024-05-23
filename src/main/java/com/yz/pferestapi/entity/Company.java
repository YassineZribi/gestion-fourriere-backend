package com.yz.pferestapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@DiscriminatorValue("C")
public class Company extends Owner {
    private String name;

    @Column(unique = true)
    private String taxId;

    public Company(String address, String phoneNumber, String email, String name, String taxId) {
        super(address, phoneNumber, email);
        this.name = name;
        this.taxId = taxId;
    }
}

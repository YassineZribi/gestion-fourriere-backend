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
@DiscriminatorValue("I")
public class Individual extends Owner {
    private String firstName;

    private String lastName;

    @Column(unique = true)
    private String nationalId;

    public Individual(String address, String phoneNumber, String email, String firstName, String lastName, String nationalId) {
        super(address, phoneNumber, email);
        this.firstName = firstName;
        this.lastName = lastName;
        this.nationalId = nationalId;
    }
}

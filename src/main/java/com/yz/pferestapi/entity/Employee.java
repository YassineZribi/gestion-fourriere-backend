package com.yz.pferestapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@DiscriminatorValue("E")
public class Employee extends User {
    //@Column(nullable = false)
    private String position; // nom du poste

    @ManyToOne
    @JoinColumn(name = "manager_id", referencedColumnName = "id")
    private Employee manager;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "institution_id")
    private Institution institution;
}

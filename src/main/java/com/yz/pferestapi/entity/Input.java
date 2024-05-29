package com.yz.pferestapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "inputs")
public class Input extends Operation {
    @Enumerated(EnumType.STRING)
    private InputStatus status = InputStatus.FULLY_IN;

    @ManyToOne
    @JoinColumn(name = "register_id")
    private Register register;

    @ManyToOne
    @JoinColumn(name = "sub_register_id")
    private SubRegister subRegister;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Owner owner;

    @ManyToOne
    @JoinColumn(name = "source_id")
    private Source source;

    @OneToMany(mappedBy = "input", fetch = FetchType.EAGER)
    private List<Output> outputs = new ArrayList<>();
}

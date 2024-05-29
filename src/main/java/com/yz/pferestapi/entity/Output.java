package com.yz.pferestapi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "outputs")
public class Output extends Operation {
    @ManyToOne
    @JoinColumn(name = "input_id")
    private Input input;
}

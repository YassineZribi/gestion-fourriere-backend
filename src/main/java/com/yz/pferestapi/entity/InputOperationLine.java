package com.yz.pferestapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "input_operation_lines")
public class InputOperationLine extends OperationLine {
    @Column(nullable = false)
    private Double nightlyAmount;

    @Column(nullable = false)
    private Double subTotalNightlyAmount;

    @Column(nullable = false)
    private Double transportFee; // frais de transport

    @Column(nullable = false)
    private Double remainingQuantity;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "input_operation_id")
    private Input input;
}

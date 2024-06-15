package com.yz.pferestapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Enumerated(EnumType.STRING)
    private ProcessingStatus status = ProcessingStatus.FULLY_IN;

    @Column(columnDefinition = "boolean default false")
    private boolean fullyOut = false;

    @Column(nullable = false)
    private String description;

    private String observation;

    private String note; // remarque

    private String photoPath;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "input_operation_id")
    private Input input;

    @JsonIgnore
    @OneToMany(mappedBy = "inputOperationLine", fetch = FetchType.EAGER)
    private List<OutputOperationLine> outputOperationLines = new ArrayList<>();
}

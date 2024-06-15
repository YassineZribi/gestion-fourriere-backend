package com.yz.pferestapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "output_operations")
public class Output extends Operation {
    @Column(nullable = false)
    private Integer nightCount;

    @Column(nullable = false)
    private Double totalTransportFee;

    @Column(nullable = false)
    private Double totalPaymentAmountWithoutDiscount;

    @Column(nullable = false)
    private Double discountAmount;

    private String discountObservation;

    @Column(nullable = false)
    private boolean discount;

    @Column(nullable = false)
    private Double totalPaymentAmount; // discount is applied

    @Column(nullable = false)
    private Integer receiptNumber;

    @Column(nullable = false)
    private Instant receiptDateTime;

    @ManyToOne
    @JoinColumn(name = "input_id")
    private Input input;

    @OneToMany(mappedBy = "output", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    List<OutputOperationLine> outputOperationLines;
}

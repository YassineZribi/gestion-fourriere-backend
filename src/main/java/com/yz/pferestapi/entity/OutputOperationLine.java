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
@Table(name = "output_operation_lines")
public class OutputOperationLine extends OperationLine {
    @Column(nullable = false)
    private Double subTotalPaymentAmount;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "output_operation_id")
    private Output output;

    @ManyToOne
    @JoinColumn(name = "input_operation_line_id")
    private InputOperationLine inputOperationLine;
}

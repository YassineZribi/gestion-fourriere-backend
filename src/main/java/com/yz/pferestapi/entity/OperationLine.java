package com.yz.pferestapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "operation_lines")
public class OperationLine {
    @EmbeddedId
    private OperationLineKey id = new OperationLineKey();

    @JsonIgnore
    @ManyToOne
    @MapsId("operationId")
    @JoinColumn(name = "operation_id")
    private Operation operation;

    @ManyToOne
    @MapsId("articleId")
    @JoinColumn(name = "article_id")
    private Article article;

    @Column(nullable = false)
    private Double quantity;

    @Column(nullable = false)
    private Double nightlyAmount;

    @Column(nullable = false)
    private Double subTotalNightlyAmount;

    @Column(nullable = false)
    private Double transportFee; // frais de transport

}

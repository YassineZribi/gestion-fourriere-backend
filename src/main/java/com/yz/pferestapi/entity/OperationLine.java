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
    Operation operation;

    @ManyToOne
    @MapsId("articleId")
    @JoinColumn(name = "article_id")
    Article article;

    Double quantity;

    Double unitPrice;

    Double lineTotalAmount; // subTotal

}

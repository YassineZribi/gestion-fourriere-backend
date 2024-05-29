package com.yz.pferestapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class OperationLineKey implements Serializable {
    @Column(name = "operation_id")
    private Long operationId;

    @Column(name = "article_id")
    private Long articleId;
}

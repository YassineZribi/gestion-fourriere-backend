package com.yz.pferestapi.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UpsertOperationLineDto {
    //@NotNull(message = "Operation id should not be null")
    //private Long operationId;

    @NotNull(message = "Article id should not be null")
    private Long articleId;

    @NotNull(message = "Quantity should not be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Quantity must be greater than 0")
    private Double quantity;
}

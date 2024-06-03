package com.yz.pferestapi.dto;

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
    private Double quantity;

    @NotNull(message = "Nightly amount should not be null")
    private Double nightlyAmount;

    @NotNull(message = "Transport fee should not be null")
    private Double transportFee;
}

package com.yz.pferestapi.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpsertInputOperationLineDto extends UpsertOperationLineDto {
    @NotNull(message = "Nightly amount should not be null")
    private Double nightlyAmount;

    @NotNull(message = "Transport fee should not be null")
    private Double transportFee;
}

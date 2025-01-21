package com.yz.pferestapi.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OperationsQuantityStatisticsCriteriaRequest {
    private Long articleFamilyId;

    private Long registerId;

    @NotNull(message = "Year should not be null")
    private Integer year;

    @Min(value = 1, message = "Month should not be less than 1")
    @Max(value = 12, message = "Month should not be greater than 12")
    private Integer month;

    private Long sourceId;
}

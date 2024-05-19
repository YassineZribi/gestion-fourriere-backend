package com.yz.pferestapi.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpsertArticleFamilyDto {
    @NotEmpty(message = "Name should not be null or empty")
    private String name;

    private String description;

    @NotNull(message = "Nightly amount should not be null")
    private Double nightlyAmount;

    @NotNull(message = "Unit calculation should not be null")
    private Boolean unitCalculation;

    @NotNull(message = "Register id should not be null")
    private Long registerId;

    @NotNull(message = "Measurement unit id should not be null")
    private Long measurementUnitId;
}

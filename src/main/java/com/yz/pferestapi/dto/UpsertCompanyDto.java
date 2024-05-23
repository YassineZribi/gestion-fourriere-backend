package com.yz.pferestapi.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpsertCompanyDto extends UpsertOwnerDto {
    @NotEmpty(message = "Name should not be null or empty")
    private String name;

    @NotEmpty(message = "Tax id should not be null or empty")
    private String taxId;
}

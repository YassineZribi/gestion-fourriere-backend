package com.yz.pferestapi.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpsertIndividualDto extends UpsertOwnerDto {
    @NotEmpty(message = "First name should not be null or empty")
    private String firstName;

    @NotEmpty(message = "Last name should not be null or empty")
    private String lastName;

    @NotEmpty(message = "National id should not be null or empty")
    private String nationalId;
}

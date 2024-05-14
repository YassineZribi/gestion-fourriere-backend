package com.yz.pferestapi.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpsertEmployeeDto {
    @NotEmpty(message = "First name should not be null or empty")
    private String firstName;

    @NotEmpty(message = "Last name should not be null or empty")
    private String lastName;

    @NotEmpty(message = "Position should not be null or empty")
    private String position;

    private Long managerId;
}

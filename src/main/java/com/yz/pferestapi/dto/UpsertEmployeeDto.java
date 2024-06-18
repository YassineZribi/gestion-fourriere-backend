package com.yz.pferestapi.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpsertEmployeeDto extends UpsertUserDto {
    @NotEmpty(message = "Position should not be null or empty")
    private String position;

    private Long managerId;
}

package com.yz.pferestapi.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpsertSubRegisterDto {
    @NotEmpty(message = "Name should not be null or empty")
    private String name;

    @NotEmpty(message = "Description should not be null or empty")
    private String description;

    @NotNull(message = "Register id should not be null")
    private Long registerId;
}

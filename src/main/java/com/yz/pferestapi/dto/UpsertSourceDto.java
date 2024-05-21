package com.yz.pferestapi.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpsertSourceDto {
    @NotEmpty(message = "Name should not be null or empty")
    private String name;

    private String description;
}

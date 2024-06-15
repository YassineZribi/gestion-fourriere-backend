package com.yz.pferestapi.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateInputOwnerDto {
    @NotNull(message = "New owner id should not be null")
    private Long newOwnerId;
}

package com.yz.pferestapi.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpsertWarehouseDto {
    @NotEmpty(message = "Name should not be null or empty")
    private String name;

    @NotEmpty(message = "Address should not be null or empty")
    private String address;

    @NotNull(message = "Latitude should not be null")
    private Double latitude;

    @NotNull(message = "Longitude should not be null")
    private Double longitude;

    private Long managerId;
}

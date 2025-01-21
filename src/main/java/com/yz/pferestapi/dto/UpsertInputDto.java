package com.yz.pferestapi.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class UpsertInputDto extends UpsertOperationDto {
    @NotEmpty(message = "Address should not be null or empty")
    private String address;

    @NotNull(message = "Latitude should not be null")
    private Double latitude;

    @NotNull(message = "Longitude should not be null")
    private Double longitude;

    @NotNull(message = "Register id should not be null")
    private Long registerId;

    @NotNull(message = "SubRegister id should not be null")
    private Long subRegisterId;

    @NotNull(message = "Owner id should not be null")
    private Long ownerId;

    @NotNull(message = "Source id should not be null")
    private Long sourceId;

    @NotEmpty(message = "Input operation line list should not be null or empty")
    private List<@Valid UpsertInputOperationLineDto> inputOperationLines;
}

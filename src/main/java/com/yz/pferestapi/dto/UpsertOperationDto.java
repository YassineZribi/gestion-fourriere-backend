package com.yz.pferestapi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpsertOperationDto {
    @NotNull(message = "Number id should not be null")
    private Long number;

    @NotNull(message = "Year id should not be null")
    private Integer year;

    @NotNull(message = "Date time is required")
    @Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}Z$",
            message = "Invalid date format. Expected format is yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    )
    private String dateTime;
}

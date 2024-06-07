package com.yz.pferestapi.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UpsertInputOperationLineDto extends UpsertOperationLineDto {
    @NotNull(message = "Nightly amount should not be null")
    private Double nightlyAmount;

    @NotNull(message = "Transport fee should not be null")
    private Double transportFee;

    @NotEmpty(message = "Description should not be null or empty")
    private String description;

    private String observation;

    private String note;

    //@NotNull(message = "Photo file should not be null")
    MultipartFile photoFile;
}

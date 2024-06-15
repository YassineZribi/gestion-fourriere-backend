package com.yz.pferestapi.dto;

import com.yz.pferestapi.entity.UpsertOutputOperationLineDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class UpsertOutputDto extends UpsertOperationDto {
    @NotNull(message = "Night count should not be null")
    private Integer nightCount;

    @NotNull(message = "Total transport fee should not be null")
    private Double totalTransportFee;

    @NotNull(message = "Total payment amount without discount should not be null")
    private Double totalPaymentAmountWithoutDiscount;

    @NotNull(message = "Discount amount should not be null")
    private Double discountAmount;

    private String discountObservation;

    @NotNull(message = "Receipt number should not be null")
    private Integer receiptNumber;

    @NotNull(message = "Receipt date time is required")
    @Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}Z$",
            message = "Invalid date format. Expected format is yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    )
    private String receiptDateTime;

    // @NotNull(message = "Input id should not be null")
    private Long inputId;

    @NotEmpty(message = "Output operation line list should not be null or empty")
    private List<@Valid UpsertOutputOperationLineDto> outputOperationLines;
}

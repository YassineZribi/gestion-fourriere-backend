package com.yz.pferestapi.entity;

import com.yz.pferestapi.dto.UpsertOperationLineDto;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpsertOutputOperationLineDto extends UpsertOperationLineDto {
    @NotNull(message = "Input operation line id should not be null")
    private Long inputOperationLineId;
}

package com.yz.pferestapi.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpsertArticleDto {
    @NotEmpty(message = "Name should not be null or empty")
    private String name;
    @NotNull(message = "Transport fee should not be null")
    private Double transportFee;
    @NotNull(message = "Article family id should not be null")
    private Long articleFamilyId;
}

package com.yz.pferestapi.dto;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class IncomeStatisticsDto {
    private Instant date;
    private Double incomes;
}

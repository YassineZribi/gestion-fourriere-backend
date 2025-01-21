package com.yz.pferestapi.dto;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CombinedQuantityDto {
    private Instant date;
    private Double inputQuantity;
    private Double outputQuantity;
}

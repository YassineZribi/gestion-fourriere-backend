package com.yz.pferestapi.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ArticleFamilyCriteriaRequest extends CriteriaRequest {
    private String name;
    private String description;
    private Double nightlyAmount;
    private Boolean unitCalculation;
    private Long registerId;
    private Long measurementUnitId;
}

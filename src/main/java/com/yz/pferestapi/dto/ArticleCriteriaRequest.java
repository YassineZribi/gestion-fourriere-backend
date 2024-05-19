package com.yz.pferestapi.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ArticleCriteriaRequest extends CriteriaRequest {
    private String name;
    private Double transportFee;
    private Long articleFamilyId;
}

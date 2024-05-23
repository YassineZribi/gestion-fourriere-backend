package com.yz.pferestapi.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CompanyCriteriaRequest extends OwnerCriteriaRequest {
    private String name;
    private String taxId;
}

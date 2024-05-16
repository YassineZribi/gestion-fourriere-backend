package com.yz.pferestapi.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SubRegisterCriteriaRequest extends CriteriaRequest {
    private String name;
    private String description;
    private Long registerId;
}

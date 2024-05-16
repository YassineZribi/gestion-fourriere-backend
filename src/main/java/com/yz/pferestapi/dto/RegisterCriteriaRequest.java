package com.yz.pferestapi.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RegisterCriteriaRequest extends CriteriaRequest {
    private String name;
    private String observation;
}

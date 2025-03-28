package com.yz.pferestapi.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class IndividualCriteriaRequest extends OwnerCriteriaRequest {
    private String firstName;
    private String lastName;
    private String nationalId;
}

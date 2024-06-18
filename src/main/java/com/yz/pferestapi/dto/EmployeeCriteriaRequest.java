package com.yz.pferestapi.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EmployeeCriteriaRequest extends UserCriteriaRequest {
    private String position;
    private Long managerId;
}

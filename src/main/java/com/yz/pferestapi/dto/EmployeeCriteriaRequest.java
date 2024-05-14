package com.yz.pferestapi.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EmployeeCriteriaRequest extends CriteriaRequest {
    private String firstName;
    private String lastName;
    private String position;
    private Long managerId;
}
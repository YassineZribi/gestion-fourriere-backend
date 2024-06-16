package com.yz.pferestapi.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class UserCriteriaRequest extends CriteriaRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String roleName;
    private String position;
    private Long managerId;
}

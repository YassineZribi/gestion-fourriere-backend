package com.yz.pferestapi.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OwnerCriteriaRequest extends CriteriaRequest {
    private String address;
    private String email;
    private String phoneNumber;
}

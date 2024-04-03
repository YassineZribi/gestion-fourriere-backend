package com.yz.pferestapi.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class UserCriteriaRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private int page = 0; // default value for page
    private int size = 10; // default value for size
    private List<String> sort;
}

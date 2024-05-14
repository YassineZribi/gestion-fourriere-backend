package com.yz.pferestapi.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class CriteriaRequest {
    private int page = 1; // default value for page
    private int size = 5; // default value for size
    private List<String> sort;
}

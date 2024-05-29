package com.yz.pferestapi.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OperationCriteriaRequest extends CriteriaRequest {
    private Long number;
    private Integer year;
    private String dateTime;
}

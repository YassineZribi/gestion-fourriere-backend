package com.yz.pferestapi.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OperationCriteriaRequest extends CriteriaRequest {
    private Long number;
    private Long startNumber;
    private Long endNumber;

    private Integer year;
    private Integer startYear;
    private Integer endYear;

    private String dateTime;
    private String startDate;
    private String endDate;
}

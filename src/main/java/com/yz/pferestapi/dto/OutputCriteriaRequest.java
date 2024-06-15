package com.yz.pferestapi.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OutputCriteriaRequest extends OperationCriteriaRequest {
    private Boolean discount;
    private Double totalPaymentAmount;
    private Integer receiptNumber;
}

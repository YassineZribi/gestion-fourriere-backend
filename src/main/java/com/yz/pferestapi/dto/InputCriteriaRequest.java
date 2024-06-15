package com.yz.pferestapi.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class InputCriteriaRequest extends OperationCriteriaRequest {
    private String status;
    private Long registerId;
    private Long subRegisterId;
    private Long ownerId;
    private Long sourceId;

    private String description; // input operation line description
}

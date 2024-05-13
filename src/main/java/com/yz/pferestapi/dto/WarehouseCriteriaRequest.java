package com.yz.pferestapi.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class WarehouseCriteriaRequest extends CriteriaRequest {
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private Long managerId;
}

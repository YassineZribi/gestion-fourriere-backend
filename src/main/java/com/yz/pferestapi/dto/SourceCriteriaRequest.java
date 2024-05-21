package com.yz.pferestapi.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SourceCriteriaRequest extends CriteriaRequest {
    private String name;
    private String description;
}

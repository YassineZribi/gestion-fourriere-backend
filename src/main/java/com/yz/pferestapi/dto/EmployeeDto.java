package com.yz.pferestapi.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmployeeDto extends UserDto {
    private String position;
    private EmployeeDto manager;
}

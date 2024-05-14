package com.yz.pferestapi.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String position;
    private String photoPath;
    private List<EmployeeDto> subordinates = new ArrayList<>();

    public void addSubordinate(EmployeeDto subordinate) {
       // if (subordinates == null) subordinates = new ArrayList<>();
        subordinates.add(subordinate);
    }
}

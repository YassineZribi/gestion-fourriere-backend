package com.yz.pferestapi.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeWithSubordinatesDto extends UserDto {
    private String position;

    private List<UserDto> subordinates = new ArrayList<>();

    public void addSubordinate(UserDto subordinate) {
        // if (subordinates == null) subordinates = new ArrayList<>();
        subordinates.add(subordinate);
    }
}

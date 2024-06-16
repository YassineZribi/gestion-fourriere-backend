package com.yz.pferestapi.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String position;
    private String photoPath;
    private List<UserDto> subordinates = new ArrayList<>();

    public void addSubordinate(UserDto subordinate) {
        // if (subordinates == null) subordinates = new ArrayList<>();
        subordinates.add(subordinate);
    }
}

package com.yz.pferestapi.dto;

import com.yz.pferestapi.entity.Role;
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
    private String photoPath;
    private RoleDto role;
    private boolean enabled;
}

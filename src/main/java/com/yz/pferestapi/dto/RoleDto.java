package com.yz.pferestapi.dto;

import com.yz.pferestapi.entity.RoleEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RoleDto {
    private Long id;
    private RoleEnum name;
}

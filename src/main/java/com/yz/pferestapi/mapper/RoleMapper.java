package com.yz.pferestapi.mapper;

import com.yz.pferestapi.dto.RoleDto;
import com.yz.pferestapi.entity.Role;

public class RoleMapper {
    public static RoleDto toDto(Role role) {
        RoleDto roleDto = new RoleDto();
        roleDto.setId(role.getId());
        roleDto.setName(role.getName());

        return roleDto;
    }
}

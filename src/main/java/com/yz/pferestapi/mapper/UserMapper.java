package com.yz.pferestapi.mapper;

import com.yz.pferestapi.dto.UpsertUserDto;
import com.yz.pferestapi.dto.UserDto;
import com.yz.pferestapi.entity.Role;
import com.yz.pferestapi.entity.User;

public class UserMapper {
    public static <T extends UpsertUserDto, U extends User> U toEntity(T upsertEntityDto, U entity, Role role) {
        entity.setFirstName(upsertEntityDto.getFirstName());
        entity.setLastName(upsertEntityDto.getLastName());
        entity.setEmail(upsertEntityDto.getEmail());
        entity.setPhoneNumber(upsertEntityDto.getPhoneNumber());
        entity.setRole(role);
        return entity;
    }
    public static <T extends User, U extends UserDto> U toDto(T entity, U entityDto) {
        entityDto.setId(entity.getId());
        entityDto.setFirstName(entity.getFirstName());
        entityDto.setLastName(entity.getLastName());
        entityDto.setEmail(entity.getEmail());
        entityDto.setPhoneNumber(entity.getPhoneNumber());
        entityDto.setPhotoPath(entity.getPhotoPath());
        return entityDto;
    };
}

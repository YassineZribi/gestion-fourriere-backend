package com.yz.pferestapi.mapper;

import com.yz.pferestapi.dto.UpsertOwnerDto;
import com.yz.pferestapi.entity.Owner;

public class OwnerMapper {
    public static <T extends UpsertOwnerDto, U extends Owner> U toEntity(T upsertEntityDto, U entity) {
        entity.setAddress(upsertEntityDto.getAddress());
        entity.setEmail(upsertEntityDto.getEmail());
        entity.setPhoneNumber(upsertEntityDto.getPhoneNumber());
        return entity;
    }
}

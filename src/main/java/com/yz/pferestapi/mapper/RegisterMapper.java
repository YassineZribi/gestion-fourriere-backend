package com.yz.pferestapi.mapper;

import com.yz.pferestapi.dto.UpsertRegisterDto;
import com.yz.pferestapi.entity.Register;

public class RegisterMapper {
    public static Register toEntity(UpsertRegisterDto upsertRegisterDto) {
        Register register = new Register();
        return map(upsertRegisterDto, register);
    }

    public static Register toEntity(UpsertRegisterDto upsertRegisterDto, Register register) {
        return map(upsertRegisterDto, register);
    }

    private static Register map(UpsertRegisterDto upsertRegisterDto, Register register) {
        register.setName(upsertRegisterDto.getName());
        register.setObservation(upsertRegisterDto.getObservation());
        return register;
    }
}

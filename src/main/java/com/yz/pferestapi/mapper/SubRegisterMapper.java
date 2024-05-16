package com.yz.pferestapi.mapper;

import com.yz.pferestapi.dto.UpsertSubRegisterDto;
import com.yz.pferestapi.entity.Register;
import com.yz.pferestapi.entity.SubRegister;

public class SubRegisterMapper {
    public static SubRegister toEntity(UpsertSubRegisterDto upsertSubRegisterDto, Register register) {
        SubRegister subRegister = new SubRegister();
        return map(upsertSubRegisterDto, register, subRegister);
    }

    public static SubRegister toEntity(UpsertSubRegisterDto upsertSubRegisterDto, Register register, SubRegister subRegister) {
        return map(upsertSubRegisterDto, register, subRegister);
    }

    private static SubRegister map(UpsertSubRegisterDto upsertSubRegisterDto, Register register, SubRegister subRegister) {
        subRegister.setName(upsertSubRegisterDto.getName());
        subRegister.setDescription(upsertSubRegisterDto.getDescription());
        subRegister.setRegister(register);
        return subRegister;
    }
}

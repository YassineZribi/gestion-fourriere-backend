package com.yz.pferestapi.service;

import com.yz.pferestapi.dto.RegisterCriteriaRequest;
import com.yz.pferestapi.dto.UpsertRegisterDto;
import com.yz.pferestapi.entity.Register;
import com.yz.pferestapi.exception.AppException;
import com.yz.pferestapi.mapper.RegisterMapper;
import com.yz.pferestapi.repository.RegisterRepository;
import com.yz.pferestapi.repository.SubRegisterRepository;
import com.yz.pferestapi.specification.RegisterSpecifications;
import com.yz.pferestapi.util.CriteriaRequestUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegisterService {
    private final RegisterRepository registerRepository;
    private final SubRegisterRepository subRegisterRepository;

    public Register getRegister(Long registerId) {
        return registerRepository.findById(registerId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Register not found"));
    }

    public List<Register> findRegistersByName(String search) {
        Specification<Register> spec = Specification.where(null);

        spec = spec.and(RegisterSpecifications.nameContains(search));

        return registerRepository.findAll(spec);
    }

    public Page<Register> findRegistersByCriteria(RegisterCriteriaRequest registerCriteria) {
        Specification<Register> spec = Specification.where(null);

        if (registerCriteria.getName() != null) {
            spec = spec.and(RegisterSpecifications.nameContains(registerCriteria.getName()));
        }

        if (registerCriteria.getObservation() != null) {
            spec = spec.and(RegisterSpecifications.observationContains(registerCriteria.getObservation()));
        }

        Sort sort = CriteriaRequestUtil.buildSortCriteria(registerCriteria);

        Pageable pageable = CriteriaRequestUtil.createPageable(registerCriteria, sort);

        return registerRepository.findAll(spec, pageable);
    }

    public Register createRegister(UpsertRegisterDto upsertRegisterDto) {
        if (registerRepository.existsByNameIgnoreCase(upsertRegisterDto.getName())) {
            throw new AppException(HttpStatus.CONFLICT, "Name already exists!");
        }

        Register register = RegisterMapper.toEntity(upsertRegisterDto);

        return registerRepository.save(register);
    }

    public Register updateRegister(UpsertRegisterDto upsertRegisterDto, Long registerId) {
        Register register = registerRepository.findById(registerId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Register not found"));

        if (registerRepository.existsByNameIgnoreCaseAndIdNot(upsertRegisterDto.getName(), registerId)) {
            throw new AppException(HttpStatus.CONFLICT, "Name already exists!");
        }

        Register updatedRegister = RegisterMapper.toEntity(upsertRegisterDto, register);

        return registerRepository.save(updatedRegister);
    }

    public void deleteRegister(Long id) {
        Register register = registerRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Register not found"));

        if (subRegisterRepository.existsByRegisterId(id)) {
            throw new AppException(HttpStatus.CONFLICT, "It is not possible to delete a Register that contains SubRegisters.");
        }

        registerRepository.delete(register);
    }
}

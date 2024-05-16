package com.yz.pferestapi.service;

import com.yz.pferestapi.dto.SubRegisterCriteriaRequest;
import com.yz.pferestapi.dto.UpsertSubRegisterDto;
import com.yz.pferestapi.entity.Register;
import com.yz.pferestapi.entity.SubRegister;
import com.yz.pferestapi.exception.AppException;
import com.yz.pferestapi.mapper.SubRegisterMapper;
import com.yz.pferestapi.repository.RegisterRepository;
import com.yz.pferestapi.repository.SubRegisterRepository;
import com.yz.pferestapi.specification.SubRegisterSpecifications;
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
public class SubRegisterService {
    private final SubRegisterRepository subRegisterRepository;
    private final RegisterRepository registerRepository;

    public SubRegister getSubRegister(Long subRegisterId) {
        return subRegisterRepository.findById(subRegisterId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "SubRegister not found"));
    }

    public List<SubRegister> findSubRegistersByName(String search) {
        Specification<SubRegister> spec = Specification.where(null);

        spec = spec.and(SubRegisterSpecifications.nameContains(search));

        return subRegisterRepository.findAll(spec);
    }

    public Page<SubRegister> findSubRegistersByCriteria(SubRegisterCriteriaRequest subRegisterCriteria) {
        Specification<SubRegister> spec = Specification.where(null);

        // Get subRegisters having a specific register
        if (subRegisterCriteria.getRegisterId() != null) {
            spec = spec.and(SubRegisterSpecifications.hasRegister(subRegisterCriteria.getRegisterId()));
        }

        if (subRegisterCriteria.getName() != null) {
            spec = spec.and(SubRegisterSpecifications.nameContains(subRegisterCriteria.getName()));
        }

        if (subRegisterCriteria.getDescription() != null) {
            spec = spec.and(SubRegisterSpecifications.descriptionContains(subRegisterCriteria.getDescription()));
        }

        Sort sort = CriteriaRequestUtil.buildSortCriteria(subRegisterCriteria);

        Pageable pageable = CriteriaRequestUtil.createPageable(subRegisterCriteria, sort);

        return subRegisterRepository.findAll(spec, pageable);
    }

    public SubRegister createSubRegister(UpsertSubRegisterDto upsertSubRegisterDto) {
        if (subRegisterRepository.existsByNameIgnoreCase(upsertSubRegisterDto.getName())) {
            throw new AppException(HttpStatus.CONFLICT, "Name already exists!");
        }

        Register register = null;
        if (upsertSubRegisterDto.getRegisterId() != null) {
            register = registerRepository.findById(upsertSubRegisterDto.getRegisterId())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Register not found"));
        }

        SubRegister subRegister = SubRegisterMapper.toEntity(upsertSubRegisterDto, register);

        return subRegisterRepository.save(subRegister);
    }

    public SubRegister updateSubRegister(UpsertSubRegisterDto upsertSubRegisterDto, Long subRegisterId) {
        SubRegister subRegister = subRegisterRepository.findById(subRegisterId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "SubRegister not found"));

        if (subRegisterRepository.existsByNameIgnoreCaseAndIdNot(upsertSubRegisterDto.getName(), subRegisterId)) {
            throw new AppException(HttpStatus.CONFLICT, "Name already exists!");
        }

        Register register = null;
        if (upsertSubRegisterDto.getRegisterId() != null) {
            register = registerRepository.findById(upsertSubRegisterDto.getRegisterId())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Register not found"));
        }

        SubRegister updatedSubRegister = SubRegisterMapper.toEntity(upsertSubRegisterDto, register, subRegister);

        return subRegisterRepository.save(updatedSubRegister);
    }

    public void deleteSubRegister(Long id) {
        SubRegister subRegister = subRegisterRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "SubRegister not found"));

        subRegisterRepository.delete(subRegister);
    }

}

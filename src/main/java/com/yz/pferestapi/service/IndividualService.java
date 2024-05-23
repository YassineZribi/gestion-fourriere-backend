package com.yz.pferestapi.service;

import com.yz.pferestapi.dto.IndividualCriteriaRequest;
import com.yz.pferestapi.dto.UpsertIndividualDto;
import com.yz.pferestapi.entity.Individual;
import com.yz.pferestapi.exception.AppException;
import com.yz.pferestapi.mapper.IndividualMapper;
import com.yz.pferestapi.repository.IndividualRepository;
import com.yz.pferestapi.specification.IndividualSpecifications;
import com.yz.pferestapi.util.CriteriaRequestUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IndividualService {
    private final IndividualRepository individualRepository;

    public Individual getIndividual(Long individualId) {
        return individualRepository.findById(individualId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Individual (owner) not found"));
    }

    public Page<Individual> findIndividualsByCriteria(IndividualCriteriaRequest individualCriteria) {
        Specification<Individual> spec = Specification.where(null);

        spec = OwnerService.filter(individualCriteria, spec);

        if (individualCriteria.getFirstName() != null) {
            spec = spec.and(IndividualSpecifications.firstNameContains(individualCriteria.getFirstName()));
        }

        if (individualCriteria.getLastName() != null) {
            spec = spec.and(IndividualSpecifications.lastNameContains(individualCriteria.getLastName()));
        }

        if (individualCriteria.getNationalId() != null) {
            spec = spec.and(IndividualSpecifications.nationalIdContains(individualCriteria.getNationalId()));
        }

        Sort sort = CriteriaRequestUtil.buildSortCriteria(individualCriteria);

        Pageable pageable = CriteriaRequestUtil.createPageable(individualCriteria, sort);

        return individualRepository.findAll(spec, pageable);
    }

    public Individual createIndividual(UpsertIndividualDto upsertIndividualDto) {
        if (individualRepository.existsByNationalIdIgnoreCase(upsertIndividualDto.getNationalId())) {
            throw new AppException(HttpStatus.CONFLICT, "National id already exists!");
        }

        Individual individual = IndividualMapper.toEntity(upsertIndividualDto);

        return individualRepository.save(individual);
    }

    public Individual updateIndividual(UpsertIndividualDto upsertIndividualDto, Long individualId) {
        Individual individual = individualRepository.findById(individualId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Individual (owner) not found"));

        if (individualRepository.existsByNationalIdIgnoreCaseAndIdNot(upsertIndividualDto.getNationalId(), individualId)) {
            throw new AppException(HttpStatus.CONFLICT, "National id already exists!");
        }

        Individual updatedIndividual = IndividualMapper.toEntity(upsertIndividualDto, individual);

        return individualRepository.save(updatedIndividual);
    }

    public void deleteIndividual(Long id) {
        Individual individual = individualRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Individual (owner) not found"));

        // TODO: if the individual has performed at least one input operation, throw an exception
//        if (InputRepository.existsByOwnerId(id)) {
//            throw new AppException(HttpStatus.CONFLICT, "It is not possible to delete a individual that has performed at least one input operation.");
//        }

        individualRepository.delete(individual);
    }
}

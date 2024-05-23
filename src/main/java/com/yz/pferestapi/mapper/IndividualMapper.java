package com.yz.pferestapi.mapper;

import com.yz.pferestapi.dto.UpsertIndividualDto;
import com.yz.pferestapi.entity.Individual;

public class IndividualMapper {
    public static Individual toEntity(UpsertIndividualDto upsertIndividualDto) {
        Individual individual = new Individual();
        return map(upsertIndividualDto, individual);
    }

    public static Individual toEntity(UpsertIndividualDto upsertIndividualDto, Individual individual) {
        return map(upsertIndividualDto, individual);
    }

    private static Individual map(UpsertIndividualDto upsertIndividualDto, Individual individual) {
        // map owner properties
        Individual mappedIndividual = OwnerMapper.toEntity(upsertIndividualDto, individual);

        mappedIndividual.setFirstName(upsertIndividualDto.getFirstName());
        mappedIndividual.setLastName(upsertIndividualDto.getLastName());
        mappedIndividual.setNationalId(upsertIndividualDto.getNationalId());

        return mappedIndividual;
    }
}

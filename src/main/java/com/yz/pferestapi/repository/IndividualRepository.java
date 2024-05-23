package com.yz.pferestapi.repository;

import com.yz.pferestapi.entity.Individual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface IndividualRepository extends JpaRepository<Individual, Long>, JpaSpecificationExecutor<Individual> {
    Boolean existsByNationalIdIgnoreCase(String nationalId);

    Boolean existsByNationalIdIgnoreCaseAndIdNot(String nationalId, Long id);
}

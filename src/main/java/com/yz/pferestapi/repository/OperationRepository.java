package com.yz.pferestapi.repository;

import com.yz.pferestapi.entity.Operation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationRepository extends JpaRepository<Operation, Long> {
    Boolean existsByNumberAndYear(Long number, Integer year);

    Boolean existsByNumberAndYearAndIdNot(Long number, Integer year, Long id);

}

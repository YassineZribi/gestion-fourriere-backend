package com.yz.pferestapi.repository;

import com.yz.pferestapi.entity.Output;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface OutputRepository extends JpaRepository<Output, Long>, JpaSpecificationExecutor<Output> {
    Boolean existsByNumberAndYear(Long number, Integer year);

    Boolean existsByNumberAndYearAndIdNot(Long number, Integer year, Long id);
}

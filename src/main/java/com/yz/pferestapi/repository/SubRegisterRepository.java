package com.yz.pferestapi.repository;

import com.yz.pferestapi.entity.SubRegister;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SubRegisterRepository extends JpaRepository<SubRegister, Long>, JpaSpecificationExecutor<SubRegister> {
    Boolean existsByNameIgnoreCase(String name);

    Boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    Boolean existsByRegisterId(Long id);
}

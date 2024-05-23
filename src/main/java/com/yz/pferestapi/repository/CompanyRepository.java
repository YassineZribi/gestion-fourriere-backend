package com.yz.pferestapi.repository;

import com.yz.pferestapi.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long>, JpaSpecificationExecutor<Company> {
    Boolean existsByTaxIdIgnoreCase(String taxId);

    Boolean existsByTaxIdIgnoreCaseAndIdNot(String taxId, Long id);
}

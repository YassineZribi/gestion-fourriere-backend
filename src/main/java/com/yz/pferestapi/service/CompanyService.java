package com.yz.pferestapi.service;

import com.yz.pferestapi.dto.CompanyCriteriaRequest;
import com.yz.pferestapi.dto.UpsertCompanyDto;
import com.yz.pferestapi.entity.Company;
import com.yz.pferestapi.exception.AppException;
import com.yz.pferestapi.mapper.CompanyMapper;
import com.yz.pferestapi.repository.CompanyRepository;
import com.yz.pferestapi.specification.CompanySpecifications;
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
public class CompanyService {
    private final CompanyRepository companyRepository;

    public Company getCompany(Long companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Company (owner) not found"));
    }

    public Page<Company> findCompaniesByCriteria(CompanyCriteriaRequest companyCriteria) {
        Specification<Company> spec = Specification.where(null);

        spec = OwnerService.filter(companyCriteria, spec);

        if (companyCriteria.getName() != null) {
            spec = spec.and(CompanySpecifications.nameContains(companyCriteria.getName()));
        }

        if (companyCriteria.getTaxId() != null) {
            spec = spec.and(CompanySpecifications.taxIdContains(companyCriteria.getTaxId()));
        }

        Sort sort = CriteriaRequestUtil.buildSortCriteria(companyCriteria);

        Pageable pageable = CriteriaRequestUtil.createPageable(companyCriteria, sort);

        return companyRepository.findAll(spec, pageable);
    }

    public Company createCompany(UpsertCompanyDto upsertCompanyDto) {
        if (companyRepository.existsByTaxIdIgnoreCase(upsertCompanyDto.getTaxId())) {
            throw new AppException(HttpStatus.CONFLICT, "Tax id already exists!");
        }

        Company company = CompanyMapper.toEntity(upsertCompanyDto);

        return companyRepository.save(company);
    }

    public Company updateCompany(UpsertCompanyDto upsertCompanyDto, Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Company (owner) not found"));

        if (companyRepository.existsByTaxIdIgnoreCaseAndIdNot(upsertCompanyDto.getTaxId(), companyId)) {
            throw new AppException(HttpStatus.CONFLICT, "Tax id already exists!");
        }

        Company updatedCompany = CompanyMapper.toEntity(upsertCompanyDto, company);

        return companyRepository.save(updatedCompany);
    }

    public void deleteCompany(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Company (owner) not found"));

        // TODO: if the company has performed at least one input operation, throw an exception
//        if (InputRepository.existsByOwnerId(id)) {
//            throw new AppException(HttpStatus.CONFLICT, "It is not possible to delete a company that has performed at least one input operation.");
//        }

        companyRepository.delete(company);
    }
}

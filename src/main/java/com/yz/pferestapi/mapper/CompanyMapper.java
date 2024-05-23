package com.yz.pferestapi.mapper;

import com.yz.pferestapi.dto.UpsertCompanyDto;
import com.yz.pferestapi.entity.Company;

public class CompanyMapper {
    public static Company toEntity(UpsertCompanyDto upsertCompanyDto) {
        Company company = new Company();
        return map(upsertCompanyDto, company);
    }

    public static Company toEntity(UpsertCompanyDto upsertCompanyDto, Company company) {
        return map(upsertCompanyDto, company);
    }

    private static Company map(UpsertCompanyDto upsertCompanyDto, Company company) {
        // map owner properties
        Company mappedCompany = OwnerMapper.toEntity(upsertCompanyDto, company);

        mappedCompany.setName(upsertCompanyDto.getName());
        mappedCompany.setTaxId(upsertCompanyDto.getTaxId());

        return mappedCompany;
    }
}

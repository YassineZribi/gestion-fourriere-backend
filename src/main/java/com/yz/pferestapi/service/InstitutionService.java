package com.yz.pferestapi.service;

import com.yz.pferestapi.dto.EmployeeWithSubordinatesDto;
import com.yz.pferestapi.dto.SaveInstitutionDto;
import com.yz.pferestapi.entity.Employee;
import com.yz.pferestapi.entity.Institution;
import com.yz.pferestapi.exception.AppException;
import com.yz.pferestapi.mapper.EmployeeMapper;
import com.yz.pferestapi.repository.EmployeeRepository;
import com.yz.pferestapi.repository.InstitutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InstitutionService {
    private final InstitutionRepository institutionRepository;
    private final FileService fileService;
    private final EmployeeRepository employeeRepository;

    public Institution getInstitution() {
        return institutionRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Institution not found"));
    }

    public Institution saveInstitution(SaveInstitutionDto saveInstitutionDto, MultipartFile photoFile) throws IOException {
        Employee employee = null;
        if (saveInstitutionDto.getChiefExecutiveId() != null) {
            employee = employeeRepository.findById(saveInstitutionDto.getChiefExecutiveId())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Employee (Chief Executive) not found"));
        }

        Optional<Institution> optionalInstitution = institutionRepository.findFirstByOrderByIdAsc();

        Institution institution = optionalInstitution.orElseGet(Institution::new);

        if (photoFile != null && !photoFile.isEmpty()) {
            String logoPath = fileService.uploadFile(photoFile, "logos");

            String oldLogoPath = institution.getLogoPath();
            if (oldLogoPath != null && !oldLogoPath.isEmpty()) {
                fileService.deleteFile(oldLogoPath);
            }

            institution.setLogoPath(logoPath);
        }

        institution.setName(saveInstitutionDto.getName());
        institution.setAddress(saveInstitutionDto.getAddress());
        institution.setEmail(saveInstitutionDto.getEmail());
        institution.setPhoneNumber(saveInstitutionDto.getPhoneNumber());
        institution.setChiefExecutive(employee);

        return institutionRepository.save(institution);
    }

    // get chief executive with recursive subordinates
    public EmployeeWithSubordinatesDto getOrganizationalChart() {
        Institution institution = institutionRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Institution not found"));
        Employee chiefExecutive = institution.getChiefExecutive();
        if (chiefExecutive == null)
            throw new AppException(HttpStatus.NOT_FOUND, "The institution has not yet selected or appointed a Chief Executive Officer.");

        EmployeeWithSubordinatesDto employeeWithSubordinatesDto = EmployeeMapper.toEmployeeWithSubordinatesDto(chiefExecutive);
        buildSubordinates(employeeWithSubordinatesDto);
        return employeeWithSubordinatesDto;
    }

    private void buildSubordinates(EmployeeWithSubordinatesDto manager) {
        List<Employee> subordinates = employeeRepository.findByManagerId(manager.getId());
        for (Employee subordinate : subordinates) {
            EmployeeWithSubordinatesDto subordinateDto = EmployeeMapper.toEmployeeWithSubordinatesDto(subordinate);
            buildSubordinates(subordinateDto);
            manager.addSubordinate(subordinateDto);
        }
    }
}
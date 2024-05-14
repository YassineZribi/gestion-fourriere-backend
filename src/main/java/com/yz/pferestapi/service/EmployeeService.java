package com.yz.pferestapi.service;

import com.yz.pferestapi.dto.EmployeeCriteriaRequest;
import com.yz.pferestapi.dto.EmployeeDto;
import com.yz.pferestapi.dto.UpsertEmployeeDto;
import com.yz.pferestapi.entity.Employee;
import com.yz.pferestapi.entity.Institution;
import com.yz.pferestapi.entity.Warehouse;
import com.yz.pferestapi.exception.AppException;
import com.yz.pferestapi.mapper.EmployeeMapper;
import com.yz.pferestapi.repository.EmployeeRepository;
import com.yz.pferestapi.repository.InstitutionRepository;
import com.yz.pferestapi.repository.WarehouseRepository;
import com.yz.pferestapi.specification.EmployeeSpecifications;
import com.yz.pferestapi.util.CriteriaRequestUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private static final String EMPLOYEES_PHOTOS_SUB_FOLDER_NAME = "employees-photos";
    private final EmployeeRepository employeeRepository;
    private final InstitutionRepository institutionRepository;
    private final WarehouseRepository warehouseRepository;
    private final FileService fileService;

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();

    }

    public EmployeeDto getEmployeeWithRecursiveSubordinates(Long id) {
        Employee manager = employeeRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Employee (Manager) not found"));

        EmployeeDto employeeDto = EmployeeMapper.toDto(manager);
        buildSubordinates(employeeDto);
        return employeeDto;
    }

    public EmployeeDto getChiefExecutiveWithRecursiveSubordinates() {
        Institution institution = institutionRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Institution not found"));
        Employee chiefExecutive = institution.getChiefExecutive();
        if (chiefExecutive == null)
            throw new AppException(HttpStatus.NOT_FOUND, "The institution has not yet selected or appointed a Chief Executive Officer.");

        EmployeeDto employeeDto = EmployeeMapper.toDto(chiefExecutive);
        buildSubordinates(employeeDto);
        return employeeDto;
    }

    private void buildSubordinates(EmployeeDto manager) {
        List<Employee> subordinates = employeeRepository.findByManagerId(manager.getId());
        for (Employee subordinate : subordinates) {
            EmployeeDto subordinateDto = EmployeeMapper.toDto(subordinate);
            buildSubordinates(subordinateDto);
            manager.addSubordinate(subordinateDto);
        }
    }

    public Employee getEmployee(Long employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Employee not found"));
    }

    public List<Employee> findEmployeesByFullName(String search) {
        Specification<Employee> spec = Specification.where(null);

        spec = spec.and(EmployeeSpecifications.fullNameContains(search));

        return employeeRepository.findAll(spec);
    }

    public Page<Employee> findEmployeesByCriteria(EmployeeCriteriaRequest employeeCriteria) {
        Specification<Employee> spec = Specification.where(null);

        // Get employees having a specific manager
        if (employeeCriteria.getManagerId() != null) {
            spec = spec.and(EmployeeSpecifications.hasManager(employeeCriteria.getManagerId()));
        }

        if (employeeCriteria.getFirstName() != null) {
            spec = spec.and(EmployeeSpecifications.firstNameContains(employeeCriteria.getFirstName()));
        }

        if (employeeCriteria.getLastName() != null) {
            spec = spec.and(EmployeeSpecifications.lastNameContains(employeeCriteria.getLastName()));
        }

        if (employeeCriteria.getPosition() != null) {
            spec = spec.and(EmployeeSpecifications.positionContains(employeeCriteria.getPosition()));
        }

        Sort sort = CriteriaRequestUtil.buildSortCriteria(employeeCriteria);

        Pageable pageable = CriteriaRequestUtil.createPageable(employeeCriteria, sort);

        return employeeRepository.findAll(spec, pageable);
    }

    public Employee createEmployee(UpsertEmployeeDto upsertEmployeeDto, MultipartFile photoFile) throws IOException {
        Institution institution = institutionRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Institution not found"));

        Employee manager = null;
        if (upsertEmployeeDto.getManagerId() != null) {
            manager = employeeRepository.findById(upsertEmployeeDto.getManagerId())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Employee (Manager) not found"));
        }

        Employee employee = EmployeeMapper.toEntity(upsertEmployeeDto, manager);

        if (photoFile != null && !photoFile.isEmpty()) {
            String photoPath = fileService.uploadFile(photoFile, EMPLOYEES_PHOTOS_SUB_FOLDER_NAME);
            employee.setPhotoPath(photoPath);
        }

        employee.setInstitution(institution);
        return employeeRepository.save(employee);
    }

    public Employee updateEmployee(UpsertEmployeeDto upsertEmployeeDto, Long employeeId, MultipartFile photoFile) throws IOException {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Employee not found"));

        Employee manager = null;
        if (upsertEmployeeDto.getManagerId() != null) {
            if (upsertEmployeeDto.getManagerId().equals(employeeId)) {
                throw new AppException(HttpStatus.BAD_REQUEST, "An employee cannot be the manager of itself");
            }
            manager = employeeRepository.findById(upsertEmployeeDto.getManagerId())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Employee (Manager) not found"));
        }

        Employee mappedEmployee = EmployeeMapper.toEntity(upsertEmployeeDto, manager, employee);

        if (photoFile != null && !photoFile.isEmpty()) {
            String photoPath = fileService.uploadFile(photoFile, EMPLOYEES_PHOTOS_SUB_FOLDER_NAME);

            String oldPhotoPath = employee.getPhotoPath();
            if (oldPhotoPath != null && !oldPhotoPath.isEmpty()) {
                fileService.deleteFile(oldPhotoPath);
            }

            mappedEmployee.setPhotoPath(photoPath);
        }

        return employeeRepository.save(mappedEmployee);
    }

    @Transactional
    public void deleteEmployee(Long id) throws IOException {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Employee not found"));

        List<Employee> subordinates = employeeRepository.findByManagerId(employee.getId());
        for (Employee subordinate : subordinates) {
            subordinate.setManager(null);
        }

        List<Warehouse> warehouses = warehouseRepository.findByManagerId(employee.getId());
        for (Warehouse warehouse : warehouses) {
            warehouse.setManager(null);
        }

        employeeRepository.delete(employee);

        String photoPath = employee.getPhotoPath();
        System.out.println("photoPath = " + photoPath);
        if (photoPath != null && !photoPath.isEmpty()) {
            fileService.deleteFile(photoPath);
        }
    }
}
